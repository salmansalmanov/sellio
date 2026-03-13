package com.sellio.repository;

import com.sellio.model.entity.PropertyDependencyEntity;
import com.sellio.model.entity.PropertyValueEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PropertyDependencyRepository extends JpaRepository<PropertyDependencyEntity, UUID> {
    boolean existsByParentIdAndChildId(UUID parentValueId, UUID childValueId);

    List<PropertyDependencyEntity> findAllByParentId(UUID parentId);

    Page<PropertyDependencyEntity> findAllByParent(PropertyValueEntity parentPropertyValueEntity, Pageable pageable);
}
