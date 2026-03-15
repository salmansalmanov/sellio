package com.sellio.mapper;

import com.sellio.model.entity.RefreshTokenEntity;
import com.sellio.model.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
public class RefreshTokenMapper {

    @Value("${spring.jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    public RefreshTokenEntity toEntity(UserEntity userEntity, UUID refreshToken) {
        return RefreshTokenEntity.builder()
                .refreshToken(refreshToken)
                .user(userEntity)
                .revoked(false)
                .expireDate(LocalDateTime.now().plus(refreshTokenExpiration, ChronoUnit.MILLIS))
                .build();
    }
}
