package com.dhanush.rentify_backend.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileResponse {

    private String fullName;

    private String phoneNumber;

    private String email;

    private String whatsAppNumber;
}