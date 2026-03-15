package com.sellio.repository;

import com.sellio.model.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CityRepository extends JpaRepository<CityEntity, UUID> {
}
