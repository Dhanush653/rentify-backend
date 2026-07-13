package com.dhanush.rentify_backend.controller;

import com.dhanush.rentify_backend.dto.user.UpdateProfileRequest;
import com.dhanush.rentify_backend.dto.user.UserProfileResponse;
import com.dhanush.rentify_backend.service.UserService;
import com.dhanush.rentify_backend.utils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile() {
        UserProfileResponse response = userService.getProfile();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Profile fetched successfully", response));
    }

    @PutMapping("profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse response = userService.updateUserProfile(request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Profile updated successfully", response));
    }
}
