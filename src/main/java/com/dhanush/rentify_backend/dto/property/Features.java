package com.dhanush.rentify_backend.dto.property;

import com.dhanush.rentify_backend.entity.enums.FurnishingType;
import com.dhanush.rentify_backend.entity.enums.PreferredTenant;
import com.dhanush.rentify_backend.entity.enums.WaterSupply;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Features {

    private Integer bedrooms;
    private Integer bathrooms;
    private Integer balconies;

    private Integer builtUpArea;

    private Integer floorNumber;
    private Integer totalFloors;

    private Boolean carParking;
    private Boolean bikeParking;

    private FurnishingType furnishingType;
    private PreferredTenant preferredTenant;
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
