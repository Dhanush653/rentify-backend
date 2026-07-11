package com.dhanush.rentify_backend.dto.property;

import com.dhanush.rentify_backend.entity.enums.PropertyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreatePropertyRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private BigDecimal rent;

    private BigDecimal deposit;

    @NotBlank
    private String city;

    private String area;

    @NotNull
    private PropertyType propertyType;
}
