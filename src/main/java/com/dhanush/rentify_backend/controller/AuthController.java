package com.dhanush.rentify_backend.controller;

import com.dhanush.rentify_backend.dto.auth.AuthResponse;
import com.dhanush.rentify_backend.dto.auth.LoginRequest;
import com.dhanush.rentify_backend.dto.auth.RegisterRequest;
import com.dhanush.rentify_backend.service.AuthService;
import com.dhanush.rentify_backend.utils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "User registered successfully", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Login successful", response));
    }
}
