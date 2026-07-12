package com.dhanush.rentify_backend.dto.property;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyImageResponse {

    private Long id;

    private String imageUrl;

    private Integer displayOrder;
}