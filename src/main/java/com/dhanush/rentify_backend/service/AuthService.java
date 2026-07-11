package com.dhanush.rentify_backend.service;

import com.dhanush.rentify_backend.dto.auth.AuthResponse;
import com.dhanush.rentify_backend.dto.auth.LoginRequest;
import com.dhanush.rentify_backend.dto.auth.RegisterRequest;
import com.dhanush.rentify_backend.entity.User;
import com.dhanush.rentify_backend.exception.InvalidCredentialsException;
import com.dhanush.rentify_backend.exception.UserAlreadyExistsException;
import com.dhanush.rentify_backend.repository.UserRepository;
import com.dhanush.rentify_backend.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public void register(RegisterRequest registerRequest) {

        if (userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
            throw new UserAlreadyExistsException("Phone Number Already Registered");
        }

        if (userRepository.existsByPhoneNumber(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email Already Registered");
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

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid phone number or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid phone number or password");
        }

        String token = jwtService.generateToken(request.getPhoneNumber());

        return new AuthResponse(token, user.getFullName(), user.getPhoneNumber());
    }
}
