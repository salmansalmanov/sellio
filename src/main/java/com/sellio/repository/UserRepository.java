package com.sellio.repository;

import com.sellio.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) > 0 FROM UserEntity u JOIN u.phoneNumbers p WHERE p = :phoneNumber")
    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT COUNT(u) > 0 FROM UserEntity u JOIN u.phoneNumbers p WHERE p IN :phoneNumbers")
    boolean existsByPhoneNumbers(Set<String> phoneNumbers);
}
