package com.dhanush.rentify_backend.repository;

import com.dhanush.rentify_backend.entity.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {

    List<PropertyImage> findByPropertyId(Long propertyId);
}
