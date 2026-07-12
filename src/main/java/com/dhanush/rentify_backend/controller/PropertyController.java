package com.dhanush.rentify_backend.controller;

import com.dhanush.rentify_backend.dto.property.CreatePropertyRequest;
import com.dhanush.rentify_backend.dto.property.PropertyResponse;
import com.dhanush.rentify_backend.dto.property.PropertySearchRequest;
import com.dhanush.rentify_backend.entity.enums.PropertyType;
import com.dhanush.rentify_backend.repository.PropertyRepository;
import com.dhanush.rentify_backend.service.PropertyService;
import com.dhanush.rentify_backend.utils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;
    @Autowired
    private PropertyRepository propertyRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<PropertyResponse>> createProperty(@Valid @RequestBody CreatePropertyRequest request) {
        PropertyResponse response = propertyService.createProperty(request);
        return ResponseEntity.status(HttpStatus.CREATED) .body(new ApiResponse<>(HttpStatus.CREATED.value(),"Property created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertyResponse>>> getAllProperties(PropertySearchRequest request) {
        List<PropertyResponse> response = propertyService.getAllProperties(request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Properties fetched successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyResponse>> getPropertyById(@PathVariable Long id) {
        PropertyResponse response = propertyService.getPropertyById(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Property fetched successfully", response));
    }

    @GetMapping("/my-properties")
    public ResponseEntity<ApiResponse<List<PropertyResponse>>> getMyProperties() {
        List<PropertyResponse> response = propertyService.getMyProperties();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Properties fetched successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyResponse>> updateProperty(@PathVariable Long id, @Valid @RequestBody CreatePropertyRequest request) {
        PropertyResponse response = propertyService.updateProperty(id, request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Property updated successfully",response));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProperty(@PathVariable Long propertyId) {
        propertyService.deleteProperty(propertyId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Property deleted successfully", null));
    }
}
