package com.sellio.repository;

import com.sellio.model.entity.PropertyEntity;
import com.sellio.model.entity.SubcategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PropertyRepository extends JpaRepository<PropertyEntity, UUID> {
    Page<PropertyEntity> findAllBySubcategory(SubcategoryEntity subcategory, Pageable pageable);
}
