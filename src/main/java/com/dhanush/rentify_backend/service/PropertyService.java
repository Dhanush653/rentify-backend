package com.dhanush.rentify_backend.service;

import com.dhanush.rentify_backend.dto.property.CreatePropertyRequest;
import com.dhanush.rentify_backend.dto.property.PropertyResponse;
import com.dhanush.rentify_backend.dto.property.PropertySearchRequest;
import com.dhanush.rentify_backend.entity.Property;
import com.dhanush.rentify_backend.entity.User;
import com.dhanush.rentify_backend.entity.enums.ListingStatus;
import com.dhanush.rentify_backend.entity.enums.PropertyType;
import com.dhanush.rentify_backend.exception.ResourceNotFoundException;
import com.dhanush.rentify_backend.repository.PropertyRepository;
import com.dhanush.rentify_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PropertyService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    public PropertyResponse createProperty(CreatePropertyRequest request) {
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

        PropertyResponse response = new PropertyResponse();

        response.setId(savedProperty.getId());
        response.setTitle(savedProperty.getTitle());
        response.setDescription(savedProperty.getDescription());
        response.setRent(savedProperty.getRent());
        response.setDeposit(savedProperty.getDeposit());
        response.setCity(savedProperty.getCity());
        response.setArea(savedProperty.getArea());
        response.setPropertyType(savedProperty.getPropertyType());
        response.setStatus(savedProperty.getStatus());
        response.setOwnerName(owner.getFullName());
        response.setOwnerPhone(owner.getPhoneNumber());

        return response;
    }

    public List<PropertyResponse> getAllProperties(PropertySearchRequest request) {
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
                .map(this::mapToPropertyResponse)
                .toList();
    }

    public PropertyResponse getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Property not found"));

        return mapToPropertyResponse(property);
    }

    private PropertyResponse mapToPropertyResponse(Property property) {
        PropertyResponse response = new PropertyResponse();

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

        return response;
    }

    public List<PropertyResponse> getMyProperties() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phoneNumber = authentication.getName();
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Property> properties = propertyRepository.findByOwner(user);

        return properties.stream()
                .map(this::mapToPropertyResponse)
                .toList();
    }

    public PropertyResponse updateProperty(Long propertyId, CreatePropertyRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phoneNumber = authentication.getName();

        User loggedInUser = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!property.getOwner().getId().equals(loggedInUser.getId())) {
            throw new ResourceNotFoundException("You are not allowed to update this property");
        }

        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setRent(request.getRent());
        property.setDeposit(request.getDeposit());
        property.setCity(request.getCity());
        property.setArea(request.getArea());
        property.setPropertyType(request.getPropertyType());
        Property updatedProperty = propertyRepository.save(property);

        return mapToPropertyResponse(updatedProperty);
    }
}
