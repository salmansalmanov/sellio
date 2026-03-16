package com.sellio.repository;

import com.sellio.model.entity.ListingEntity;
import com.sellio.model.enums.ListingStatus;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListingRepository extends JpaRepository<ListingEntity, UUID> {
    Page<ListingEntity> findAllByStatus(Pageable pageable, ListingStatus status);

    Page<ListingEntity> findAllByOwnerIdAndStatus(UUID ownerId, Pageable pageable, ListingStatus status);

    List<ListingEntity> findAllByStatusAndUpdatedAtBefore(ListingStatus status, LocalDateTime localDateTime);

    @NullMarked
    @EntityGraph(attributePaths = {
            "thumbnail",
            "owner",
            "city",
            "images",
            "subcategory",
            "listingProperties.value.property",
            "subcategory.category",
            "owner.phoneNumbers",
            "subcategory.properties"
    })
    Optional<ListingEntity> findById(UUID id);

    List<ListingEntity> findAllByOwnerIdAndStatus(UUID id, Sort sort, ListingStatus status);

    List<ListingEntity> findAllByOwnerIdAndStatus(UUID id, ListingStatus status);
}
