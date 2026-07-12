package com.dhanush.rentify_backend.dto.property;

import com.dhanush.rentify_backend.entity.enums.PropertyType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PropertyListResponse {

    private Long id;

    private String title;

    private BigDecimal rent;

    private String city;

    private String area;

    private PropertyType propertyType;

    private String thumbnail;

}
