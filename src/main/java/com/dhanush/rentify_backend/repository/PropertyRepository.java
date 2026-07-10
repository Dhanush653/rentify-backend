package com.dhanush.rentify_backend.repository;

import com.dhanush.rentify_backend.entity.Property;
import com.dhanush.rentify_backend.entity.enums.ListingStatus;
import com.dhanush.rentify_backend.entity.enums.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByOwnerId(Long ownerId);

    List<Property> findByCityIgnoreCase(String city);

    List<Property> findByPropertyType(PropertyType propertyType);

    List<Property> findByStatus(ListingStatus status);
}
