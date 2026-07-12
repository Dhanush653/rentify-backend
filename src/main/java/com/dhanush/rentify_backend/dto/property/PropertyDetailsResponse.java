package com.dhanush.rentify_backend.dto.property;

import com.dhanush.rentify_backend.entity.enums.ListingStatus;
import com.dhanush.rentify_backend.entity.enums.PropertyType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class PropertyDetailsResponse {

    private Long id;

    private String title;

    private String description;

    private BigDecimal rent;

    private BigDecimal deposit;

    private String city;

    private String area;

    private PropertyType propertyType;

    private ListingStatus status;

    private String ownerName;

    private String ownerPhone;

    private List<String> imageUrls;

}