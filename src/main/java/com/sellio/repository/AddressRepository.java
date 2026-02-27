package com.sellio.repository;

import com.sellio.model.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<AddressEntity, UUID> {
    Optional<AddressEntity> findByPlaceId(String placeId);
}
