package com.dhanush.rentify_backend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {

    @NotBlank
    private String fullName;

    @Email
    private String email;

    private String whatsAppNumber;
}