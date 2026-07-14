package com.dhanush.rentify_backend.entity;

import com.dhanush.rentify_backend.entity.enums.ListingStatus;
import com.dhanush.rentify_backend.entity.enums.PropertyType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
@Getter
@Setter
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String title;

    @Column(length = 3000)
    private String description;

    @Column(nullable = false)
    private BigDecimal rent;

    private BigDecimal deposit;

    @Column(nullable = false)
    private String city;

    private String area;

    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;

    @Enumerated(EnumType.STRING)
    private ListingStatus status;

    private LocalDateTime expiresAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @Column(nullable = false)
    private Integer contactCount = 0;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @OneToMany(
            mappedBy = "property",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PropertyImage> images = new ArrayList<>();

    @OneToOne(
            mappedBy = "property",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private PropertyFeatures features;
}