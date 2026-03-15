package com.sellio.repository;

import com.sellio.model.entity.CategoryEntity;
import com.sellio.model.entity.SubcategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubcategoryRepository extends JpaRepository<SubcategoryEntity, UUID> {
    Page<SubcategoryEntity> findAllByCategory(CategoryEntity category, Pageable pageable);
}
