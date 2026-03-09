package com.sellio.repository;

import com.sellio.model.entity.PropertyEntity;
import com.sellio.model.entity.PropertyValueEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PropertyValueRepository extends JpaRepository<PropertyValueEntity, UUID> {
    Page<PropertyValueEntity> findAllByProperty(PropertyEntity property, Pageable pageable);
}
