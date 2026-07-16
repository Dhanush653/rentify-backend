package com.dhanush.rentify_backend.service;

import com.dhanush.rentify_backend.config.SupabaseConfig;
import com.dhanush.rentify_backend.dto.property.*;
import com.dhanush.rentify_backend.entity.Property;
import com.dhanush.rentify_backend.entity.PropertyFeatures;
import com.dhanush.rentify_backend.entity.PropertyImage;
import com.dhanush.rentify_backend.entity.User;
import com.dhanush.rentify_backend.entity.enums.ListingStatus;
import com.dhanush.rentify_backend.exception.ResourceNotFoundException;
import com.dhanush.rentify_backend.repository.PropertyImageRepository;
import com.dhanush.rentify_backend.repository.PropertyRepository;
import com.dhanush.rentify_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class PropertyService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyImageRepository propertyImageRepository;

    @Autowired
    private SupabaseStorageService supabaseStorageService;

    @Autowired
    private SupabaseConfig supabaseConfig;

    @Transactional
    public PropertyDetailsResponse createProperty(CreatePropertyRequest request, List<MultipartFile> files) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String phoneNumber = authentication.getName();
        User owner = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Property property = new Property();

        property.setOwner(owner);
        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setRent(request.getRent());
        property.setDeposit(request.getDeposit());
        property.setCity(request.getCity());
        property.setArea(request.getArea());
        property.setPropertyType(request.getPropertyType());
        property.setStatus(ListingStatus.ACTIVE);
        property.setExpiresAt(request.getExpiresAt());
        property.setLatitude(request.getLatitude());
        property.setLongitude(request.getLongitude());

        if (request.getFeatures() != null) {
            PropertyFeatures features = new PropertyFeatures();
            features.setProperty(property);
            applyFeatures(features, request.getFeatures());
            property.setFeatures(features);
        }

        if (files != null && !files.isEmpty()) {
            for (int i = 0; i < files.size(); i++) {
                String imageUrl = supabaseStorageService.uploadImage(files.get(i));

                PropertyImage image = new PropertyImage();
                image.setProperty(property);
                image.setImageUrl(imageUrl);
                image.setDisplayOrder(i + 1);
                property.getImages().add(image);
            }
        }

        Property savedProperty = propertyRepository.save(property);

        return mapToPropertyDetailsResponse(savedProperty);
    }

    @Transactional(readOnly = true)
    public List<PropertyListResponse> getAllProperties(PropertySearchRequest request) {
        String city = request.getCity();
        String area = request.getArea();

        if (city != null) {
            city = city.trim().toLowerCase();
        }

        if (area != null) {
            area = area.trim().toLowerCase();
        }

        List<Property> properties = propertyRepository.searchProperties(
                city,
                area,
                request.getPropertyType(),
                request.getMinRent(),
                request.getMaxRent(),
                LocalDateTime.now()
        );

        return properties.stream()
                .map(this::mapToPropertyListResponse)
                .toList();
    }

    @Transactional
    public PropertyDetailsResponse getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Property not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            user = userRepository.findByPhoneNumber(authentication.getName())
                    .orElse(null);
        }

        if (user == null || !user.getId().equals(property.getOwner().getId())) {
            property.setViewCount(property.getViewCount() + 1);
            property = propertyRepository.save(property);
        }

        return mapToPropertyDetailsResponse(property);
    }

    @Transactional(readOnly = true)
    public List<PropertyListResponse> getMyProperties() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phoneNumber = authentication.getName();
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Property> properties = propertyRepository.findByOwner(user);

        return properties.stream()
                .map(this::mapToPropertyListResponse)
                .toList();
    }

    @Transactional
    public PropertyDetailsResponse updateProperty(Long propertyId, CreatePropertyRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phoneNumber = authentication.getName();

        User loggedInUser = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        if (!property.getOwner().getId().equals(loggedInUser.getId())) {
            throw new RuntimeException("You are not allowed to update this property");
        }

        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setRent(request.getRent());
        property.setDeposit(request.getDeposit());
        property.setCity(request.getCity());
        property.setArea(request.getArea());
        property.setLatitude(request.getLatitude());
        property.setLongitude(request.getLongitude());
        property.setPropertyType(request.getPropertyType());

        if (request.getFeatures() != null) {
            PropertyFeatures features = property.getFeatures();

            if (features == null) {
                features = new PropertyFeatures();
                features.setProperty(property);
                property.setFeatures(features);
            }

            applyFeatures(features, request.getFeatures());
        }

        Property updatedProperty = propertyRepository.save(property);

        return mapToPropertyDetailsResponse(updatedProperty);
    }

    @Transactional
    public void deleteProperty(Long propertyId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phoneNumber = authentication.getName();
        User loggedInUser = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        if (!loggedInUser.getId().equals(property.getOwner().getId())) {
            throw new RuntimeException("You are not allowed to delete this property");
        }

        propertyRepository.delete(property);
    }

    @Transactional
    public void incrementContactCount(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Property not found"));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phoneNumber = authentication.getName();

        User loggedInUser = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!loggedInUser.getId().equals(property.getOwner().getId())) {
            property.setContactCount(property.getContactCount() + 1);
            propertyRepository.save(property);
        }
    }

    private void applyFeatures(PropertyFeatures features, Features request) {
        features.setBedrooms(request.getBedrooms());
        features.setBathrooms(request.getBathrooms());
        features.setBalconies(request.getBalconies());
        features.setBuiltUpArea(request.getBuiltUpArea());

        features.setFloorNumber(request.getFloorNumber());
        features.setTotalFloors(request.getTotalFloors());

        features.setCarParking(request.getCarParking());
        features.setBikeParking(request.getBikeParking());

        features.setFurnishingType(request.getFurnishingType());
        features.setPreferredTenant(request.getPreferredTenant());
        features.setWaterSupply(request.getWaterSupply());

        features.setPropertyAge(request.getPropertyAge());

        features.setLift(request.getLift());
        features.setPowerBackup(request.getPowerBackup());
        features.setWifi(request.getWifi());
        features.setAirConditioner(request.getAirConditioner());
        features.setSecurity(request.getSecurity());
        features.setCctv(request.getCctv());
        features.setPetFriendly(request.getPetFriendly());

        features.setWashroomAvailable(request.getWashroomAvailable());
        features.setMainRoadFacing(request.getMainRoadFacing());
        features.setCornerShop(request.getCornerShop());
    }

    private Features mapToFeatures(PropertyFeatures features) {
        Features response = new Features();

        response.setBedrooms(features.getBedrooms());
        response.setBathrooms(features.getBathrooms());
        response.setBalconies(features.getBalconies());
        response.setBuiltUpArea(features.getBuiltUpArea());

        response.setFloorNumber(features.getFloorNumber());
        response.setTotalFloors(features.getTotalFloors());

        response.setCarParking(features.getCarParking());
        response.setBikeParking(features.getBikeParking());

        response.setFurnishingType(features.getFurnishingType());
        response.setPreferredTenant(features.getPreferredTenant());
        response.setWaterSupply(features.getWaterSupply());

        response.setPropertyAge(features.getPropertyAge());

        response.setLift(features.getLift());
        response.setPowerBackup(features.getPowerBackup());
        response.setWifi(features.getWifi());
        response.setAirConditioner(features.getAirConditioner());
        response.setSecurity(features.getSecurity());
        response.setCctv(features.getCctv());
        response.setPetFriendly(features.getPetFriendly());

        response.setWashroomAvailable(features.getWashroomAvailable());
        response.setMainRoadFacing(features.getMainRoadFacing());
        response.setCornerShop(features.getCornerShop());

        return response;
    }

    private String buildImageUrl(String fileName) {
        return supabaseConfig.getUrl()
                + "/storage/v1/object/public/"
                + supabaseConfig.getBucket()
                + "/"
                + fileName;
    }

    private PropertyListResponse mapToPropertyListResponse(Property property) {
        PropertyListResponse response = new PropertyListResponse();

        response.setId(property.getId());
        response.setTitle(property.getTitle());
        response.setRent(property.getRent());
        response.setCity(property.getCity());
        response.setArea(property.getArea());
        response.setPropertyType(property.getPropertyType());
        response.setBathRooms(property.getFeatures().getBathrooms());
        response.setBedRooms(property.getFeatures().getBedrooms());

        if (property.getFeatures().getBikeParking() || property.getFeatures().getCarParking()) {
            response.setIsParkingAvailable(true);
        }

        String thumbnail = null;

        if (property.getImages() != null && !property.getImages().isEmpty()) {
            PropertyImage firstImage = property.getImages()
                    .stream()
                    .sorted(Comparator.comparing(PropertyImage::getDisplayOrder))
                    .findFirst()
                    .orElse(null);

            if (firstImage != null) {
                thumbnail = buildImageUrl(firstImage.getImageUrl());
            }
        }
        response.setThumbnail(thumbnail);

        return response;
    }

    private PropertyDetailsResponse mapToPropertyDetailsResponse(Property property) {
        PropertyDetailsResponse response = new PropertyDetailsResponse();

        response.setId(property.getId());
        response.setTitle(property.getTitle());
        response.setDescription(property.getDescription());
        response.setRent(property.getRent());
        response.setDeposit(property.getDeposit());
        response.setCity(property.getCity());
        response.setArea(property.getArea());
        response.setLatitude(property.getLatitude());
        response.setLongitude(property.getLongitude());
        response.setViewCount(property.getViewCount());
        response.setContactCount(property.getContactCount());
        response.setPropertyType(property.getPropertyType());
        response.setStatus(property.getStatus());
        response.setOwnerName(property.getOwner().getFullName());
        response.setOwnerPhone(property.getOwner().getPhoneNumber());
        response.setWhatsAppNumber(property.getOwner().getWhatsappNumber());

        if (property.getFeatures() != null) {
            response.setFeatures(mapToFeatures(property.getFeatures()));
        }

        List<String> imageUrls = property.getImages()
                .stream()
                .sorted(Comparator.comparing(PropertyImage::getDisplayOrder))
                .map(image -> buildImageUrl(image.getImageUrl()))
                .toList();

        response.setImageUrls(imageUrls);
        return response;
    }

    @Transactional
    public List<PropertyImageResponse> uploadPropertyImages(Long propertyId, List<MultipartFile> files) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phoneNumber = authentication.getName();

        User loggedInUser = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        if (!property.getOwner().getId().equals(loggedInUser.getId())) {
            throw new RuntimeException("You are not allowed to upload images");
        }

        int displayOrder = property.getImages().size() + 1;

        for (MultipartFile file : files) {
            String fileName = supabaseStorageService.uploadImage(file);
            PropertyImage image = new PropertyImage();
            image.setProperty(property);
            image.setImageUrl(fileName);
            image.setDisplayOrder(displayOrder++);

            property.getImages().add(image);
        }

        Property savedProperty = propertyRepository.save(property);

        return savedProperty.getImages()
                .stream()
                .sorted(Comparator.comparing(PropertyImage::getDisplayOrder))
                .map(this::mapToPropertyImageResponse)
                .toList();
    }

    private PropertyImageResponse mapToPropertyImageResponse(PropertyImage image) {
        PropertyImageResponse response = new PropertyImageResponse();
        response.setId(image.getId());
        response.setImageUrl(buildImageUrl(image.getImageUrl()));
        response.setDisplayOrder(image.getDisplayOrder());
        return response;
    }

    @Transactional
    public void deletePropertyImage(Long imageId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phoneNumber = authentication.getName();

        User loggedInUser = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        PropertyImage image = propertyImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

        Property property = image.getProperty();
        if (!property.getOwner().getId().equals(loggedInUser.getId())) {
            throw new RuntimeException("You are not allowed to delete this image");
        }

        supabaseStorageService.deleteImage(image.getImageUrl());
        property.getImages().remove(image);
    }
}
