package com.dhanush.rentify_backend.controller;

import com.dhanush.rentify_backend.dto.property.*;
import com.dhanush.rentify_backend.service.PropertyService;
import com.dhanush.rentify_backend.utils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PropertyDetailsResponse>> createProperty(@Valid @ModelAttribute CreatePropertyRequest request, @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        PropertyDetailsResponse response = propertyService.createProperty(request, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(HttpStatus.CREATED.value(),"Property created successfully",response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertyListResponse>>> getAllProperties(PropertySearchRequest request) {
        List<PropertyListResponse> response = propertyService.getAllProperties(request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Properties fetched successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyDetailsResponse>> getPropertyById(@PathVariable Long id) {
        PropertyDetailsResponse response = propertyService.getPropertyById(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Property fetched successfully", response));
    }

    @GetMapping("/my-properties")
    public ResponseEntity<ApiResponse<List<PropertyListResponse>>> getMyProperties() {
        List<PropertyListResponse> response = propertyService.getMyProperties();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Properties fetched successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyDetailsResponse>> updateProperty(@PathVariable Long id, @Valid @RequestBody CreatePropertyRequest request) {
        PropertyDetailsResponse response = propertyService.updateProperty(id, request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Property updated successfully",response));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Property deleted successfully", null));
    }

    @PostMapping("/{id}/contact")
    public ResponseEntity<ApiResponse<Void>> contactOwner(@PathVariable Long id) {
        propertyService.incrementContactCount(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(),"Contact count updated successfully",null));
    }

    @PostMapping("/{propertyId}/images")
    public ResponseEntity<ApiResponse<List<PropertyImageResponse>>> uploadImages(@PathVariable Long propertyId, @RequestParam("files") List<MultipartFile> files) {
        List<PropertyImageResponse> response = propertyService.uploadPropertyImages(propertyId, files);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Images uploaded successfully", response));
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deletePropertyImage(@PathVariable Long imageId) {
        propertyService.deletePropertyImage(imageId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Image deleted successfully", null));
    }
}
