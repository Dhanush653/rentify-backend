package com.dhanush.rentify_backend.service;

import com.dhanush.rentify_backend.dto.user.UpdateProfileRequest;
import com.dhanush.rentify_backend.dto.user.UserProfileResponse;
import com.dhanush.rentify_backend.entity.User;
import com.dhanush.rentify_backend.exception.ResourceNotFoundException;
import com.dhanush.rentify_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserProfileResponse getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phoneNumber = authentication.getName();

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setFullName(user.getFullName());
        userProfileResponse.setEmail(user.getEmail());
        userProfileResponse.setPhoneNumber(user.getPhoneNumber());
        userProfileResponse.setWhatsAppNumber(user.getWhatsappNumber());

        return userProfileResponse;
    }

    public UserProfileResponse updateUserProfile(UpdateProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phoneNumber = authentication.getName();

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setFullName(request.getFullName());
        user.setWhatsappNumber(request.getWhatsAppNumber());
        user.setEmail(request.getEmail());

        userRepository.save(user);

        UserProfileResponse response = new UserProfileResponse();
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setWhatsAppNumber(user.getWhatsappNumber());

        return response;
    }
}
