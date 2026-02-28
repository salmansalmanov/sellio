package com.sellio.repository;

import com.sellio.model.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends JpaRepository<AdminEntity, UUID> {
    boolean existsByUsername(String username);

    Optional<AdminEntity> findByUsername(String username);
}
