package com.sellio.repository;

import com.sellio.model.entity.ImageEntity;
import com.sellio.model.entity.ShopEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ShopRepository extends JpaRepository<ShopEntity, UUID> {
    boolean existsByName(String name);

    @NullMarked
    @EntityGraph(value = "ShopWithAddresses", type = EntityGraph.EntityGraphType.LOAD)
    Page<ShopEntity> findAll(Pageable pageable);

    boolean existsById(UUID id);

    @Modifying
    @Query("UPDATE ShopEntity s SET s.logo = :image WHERE s.id = :shopId")
    void updateLogo(@Param("shopId") UUID shopId, @Param("image") ImageEntity image);

    @Modifying
    @Query("UPDATE ShopEntity s SET s.banner = :image WHERE s.id = :shopId")
    void updateBanner(@Param("shopId") UUID shopId, @Param("image") ImageEntity image);
}
