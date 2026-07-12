package com.dhanush.rentify_backend.service;

import com.dhanush.rentify_backend.config.SupabaseConfig;
import com.dhanush.rentify_backend.dto.property.*;
import com.dhanush.rentify_backend.entity.Property;
import com.dhanush.rentify_backend.entity.PropertyImage;
import com.dhanush.rentify_backend.entity.User;
import com.dhanush.rentify_backend.entity.enums.ListingStatus;
import com.dhanush.rentify_backend.exception.ResourceNotFoundException;
import com.dhanush.rentify_backend.repository.PropertyImageRepository;
import com.dhanush.rentify_backend.repository.PropertyRepository;
import com.dhanush.rentify_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
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

    public PropertyDetailsResponse createProperty(CreatePropertyRequest request, List<MultipartFile> files) {
        System.out.println("Title: " + request.getTitle());
        System.out.println("Rent: " + request.getRent());
        System.out.println("City: " + request.getCity());
        System.out.println("Property Type: " + request.getPropertyType());
        System.out.println("Files: " + (files == null ? 0 : files.size()));
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
        property.setExpiresAt(LocalDateTime.now().plusDays(30));
        Property savedProperty = propertyRepository.save(property);

        if (files != null && !files.isEmpty()) {
            for (int i = 0; i < files.size(); i++) {
                String imageUrl = supabaseStorageService.uploadImage(files.get(i));

                PropertyImage image = new PropertyImage();
                image.setProperty(savedProperty);
                image.setImageUrl(imageUrl);
                image.setDisplayOrder(i + 1);
                propertyImageRepository.save(image);
            }
        }

        return mapToPropertyDetailsResponse(savedProperty);
    }

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
                request.getMaxRent()
        );

        return properties.stream()
                .map(this::mapToPropertyListResponse)
                .toList();
    }

    public PropertyDetailsResponse getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Property not found"));

        return mapToPropertyDetailsResponse(property);
    }

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
        property.setPropertyType(request.getPropertyType());
        Property updatedProperty = propertyRepository.save(property);

        return mapToPropertyDetailsResponse(updatedProperty);
    }

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

    public void incrementContactCount(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Property not found"));
        property.setContactCount(property.getContactCount() + 1);
        propertyRepository.save(property);
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
        response.setPropertyType(property.getPropertyType());
        response.setStatus(property.getStatus());
        response.setOwnerName(property.getOwner().getFullName());
        response.setOwnerPhone(property.getOwner().getPhoneNumber());

        List<String> imageUrls = property.getImages()
                .stream()
                .sorted(Comparator.comparing(PropertyImage::getDisplayOrder))
                .map(image -> buildImageUrl(image.getImageUrl()))
                .toList();

        response.setImageUrls(imageUrls);
        return response;
    }
}
