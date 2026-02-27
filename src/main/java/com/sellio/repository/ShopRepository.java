package com.sellio.repository;

import com.sellio.model.entity.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShopRepository extends JpaRepository<ShopEntity, UUID> {
    boolean existsByName(String name);
}
