package com.sellio.repository;

import com.sellio.model.entity.ShopEntity;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShopRepository extends JpaRepository<ShopEntity, UUID> {
    boolean existsByName(String name);

    @NullMarked
    @EntityGraph(value = "ShopWithAddresses", type = EntityGraph.EntityGraphType.LOAD)
    Page<ShopEntity> findAll(Pageable pageable);

    boolean existsById(UUID id);
}
