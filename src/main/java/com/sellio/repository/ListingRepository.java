package com.sellio.repository;

import com.sellio.model.entity.ListingEntity;
import com.sellio.model.enums.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ListingRepository extends JpaRepository<ListingEntity, UUID> {
    Page<ListingEntity> findAllByStatus(Pageable pageable, ListingStatus status);

    Page<ListingEntity> findAllByOwnerIdAndStatus(UUID ownerId, Pageable pageable, ListingStatus status);
}
