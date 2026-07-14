package com.dhanush.rentify_backend.entity;

import com.dhanush.rentify_backend.entity.enums.FurnishingType;
import com.dhanush.rentify_backend.entity.enums.PreferredTenant;
import com.dhanush.rentify_backend.entity.enums.WaterSupply;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "property_features")
@Getter
@Setter
public class PropertyFeatures {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false, unique = true)
    private Property property;

    private Integer bedrooms;

    private Integer bathrooms;

    private Integer balconies;

    private Integer builtUpArea;

    private Integer floorNumber;

    private Integer totalFloors;

    private Boolean carParking;

    private Boolean bikeParking;

    @Enumerated(EnumType.STRING)
    private FurnishingType furnishingType;

    @Enumerated(EnumType.STRING)
    private PreferredTenant preferredTenant;

    @Enumerated(EnumType.STRING)
    private WaterSupply waterSupply;

    private Integer propertyAge;

    private Boolean lift;

    private Boolean powerBackup;

    private Boolean wifi;

    private Boolean airConditioner;

    private Boolean security;

    private Boolean cctv;

    private Boolean petFriendly;
}
