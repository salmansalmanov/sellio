package com.sellio.service.impl;

import com.sellio.exception.custom.InvalidInputException;
import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.factory.concrete.UserServiceFactory;
import com.sellio.mapper.RefreshTokenMapper;
import com.sellio.model.dto.request.LoginRequest;
import com.sellio.model.dto.request.RegisterRequest;
import com.sellio.model.dto.request.TokenRefreshRequest;
import com.sellio.model.dto.response.core.LoginResponse;
import com.sellio.model.dto.response.core.TokenRefreshResponse;
import com.sellio.model.dto.response.core.UserResponse;
import com.sellio.model.entity.AdminEntity;
import com.sellio.model.entity.RefreshTokenEntity;
import com.sellio.model.entity.UserEntity;
import com.sellio.model.enums.Role;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.SuccessDataResult;
import com.sellio.repository.RefreshTokenRepository;
import com.sellio.repository.UserRepository;
import com.sellio.service.abstraction.AuthService;
import com.sellio.service.abstraction.UserService;
import com.sellio.util.JwtUtil;
import com.sellio.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserUtil userUtil;
    private final UserServiceFactory userServiceFactory;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    @Value("${spring.jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    @Override
    public DataResult<UserResponse> register(RegisterRequest registerRequest, String role, MultipartFile logo, MultipartFile banner) throws IOException {
        log.info("AuthService.register.start: {}", registerRequest);
        userUtil.checkUser(registerRequest);
        Role roleEnum;
        try {
            roleEnum = Role.valueOf(role.toUpperCase());
        } catch (Exception e) {
            throw new InvalidInputException("Invalid role");
        }
        UserService userService = userServiceFactory.getServiceByRole(roleEnum);
        log.info("AuthService.register.end: {}", userService);
        return userService.save(registerRequest, logo, banner);
    }

    @Override
    @Transactional
    public DataResult<LoginResponse> login(LoginRequest loginRequest) {
        log.info("AuthService.login.start: {}", loginRequest);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        UserEntity userEntity = userRepository.findByIdentifier(loginRequest.getUsernameOrEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String principal = userEntity.getRole() == Role.ADMIN || userEntity.getRole() == Role.SUPER_ADMIN ?
                ((AdminEntity) Hibernate.unproxy(userEntity)).getUsername() : userEntity.getEmail();

        String accessToken = jwtUtil.generateAccessToken(principal, userEntity.getRole());
        UUID refreshToken = UUID.randomUUID();

        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByUser(userEntity)
                .map(foundRefreshTokenEntity -> {
                    foundRefreshTokenEntity.setRefreshToken(refreshToken);
                    foundRefreshTokenEntity.setExpireDate(LocalDateTime.now().plus(refreshTokenExpiration, ChronoUnit.MILLIS));
                    foundRefreshTokenEntity.setIsRevoked(false);
                    return foundRefreshTokenEntity;
                })
                .orElseGet(() -> refreshTokenMapper.toEntity(userEntity, refreshToken));
        refreshTokenRepository.save(refreshTokenEntity);
        LoginResponse loginResponse = new LoginResponse(accessToken, refreshToken);
        log.info("AuthService.login.end: {}", loginResponse);
        return new SuccessDataResult<>(loginResponse, "Login successful");
    }

    @Override
    @Transactional
    public DataResult<TokenRefreshResponse> refreshToken(TokenRefreshRequest request) {
        log.info("AuthService.refreshToken.start: {}", request);
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));
        jwtUtil.checkRefreshToken(refreshTokenEntity);

        UserEntity userEntity = refreshTokenEntity.getUser();
        String principal = userEntity.getEmail() != null ? userEntity.getEmail() : ((AdminEntity) Hibernate.unproxy(userEntity)).getUsername();
        String newAccessToken = jwtUtil.generateAccessToken(principal, userEntity.getRole());
        UUID newRefreshToken = UUID.randomUUID();

        refreshTokenEntity.setRefreshToken(newRefreshToken);
        refreshTokenEntity.setExpireDate(LocalDateTime.now().plus(refreshTokenExpiration, ChronoUnit.MILLIS));
        refreshTokenRepository.save(refreshTokenEntity);

        TokenRefreshResponse refreshTokenResponse = new TokenRefreshResponse(newAccessToken, newRefreshToken);
        log.info("AuthService.refreshToken.end: {}", refreshTokenResponse);
        return new SuccessDataResult<>(refreshTokenResponse, "Refresh token successful");
    }
}
