package com.dhanush.rentify_backend.repository;

import com.dhanush.rentify_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhoneNumber(String number);

    Optional<User> findByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);
}
