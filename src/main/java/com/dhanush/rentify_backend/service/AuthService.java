package com.dhanush.rentify_backend.service;

import com.dhanush.rentify_backend.dto.auth.RegisterRequest;
import com.dhanush.rentify_backend.entity.User;
import com.dhanush.rentify_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void register(RegisterRequest registerRequest) {

        if (userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
            throw new RuntimeException("Phone Number Already Registered");
        }

        if (userRepository.existsByPhoneNumber(registerRequest.getEmail())) {
            throw new RuntimeException("Email Already Registered");
        }

        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setActive(true);
        user.setEmail(registerRequest.getEmail());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setWhatsappNumber(registerRequest.getWhatsAppNumber());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        userRepository.save(user);
    }
}
