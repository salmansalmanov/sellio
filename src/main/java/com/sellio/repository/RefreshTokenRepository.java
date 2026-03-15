package com.sellio.repository;

import com.sellio.model.entity.RefreshTokenEntity;
import com.sellio.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    Optional<RefreshTokenEntity> findByUser(UserEntity user);

    Optional<RefreshTokenEntity> findByRefreshToken(UUID refreshToken);

    void deleteByUser(UserEntity user);
}
