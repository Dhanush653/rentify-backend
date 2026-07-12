package com.dhanush.rentify_backend.repository;

import com.dhanush.rentify_backend.entity.Property;
import com.dhanush.rentify_backend.entity.User;
import com.dhanush.rentify_backend.entity.enums.ListingStatus;
import com.dhanush.rentify_backend.entity.enums.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByOwnerId(Long ownerId);

    List<Property> findByCityIgnoreCase(String city);

    List<Property> findByPropertyType(PropertyType propertyType);

    List<Property> findByStatus(ListingStatus status);

    List<Property> findByOwner(User user);

    @Query("""
            SELECT p
            FROM Property p
            WHERE
                (:city IS NULL OR LOWER(p.city) = :city)
            AND (:area IS NULL OR LOWER(p.area) = :area)
            AND (:propertyType IS NULL OR p.propertyType = :propertyType)
            AND (:minRent IS NULL OR p.rent >= :minRent)
            AND (:maxRent IS NULL OR p.rent <= :maxRent)
            ORDER BY p.createdAt DESC
            """)
    List<Property> searchProperties(String city, String area, PropertyType propertyType, BigDecimal minRent, BigDecimal maxRent);
}
