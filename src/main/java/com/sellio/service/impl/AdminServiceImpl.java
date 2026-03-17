package com.sellio.service.impl;

import com.sellio.exception.custom.AlreadyExistsException;
import com.sellio.exception.custom.InvalidInputException;
import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.mapper.AdminMapper;
import com.sellio.model.dto.request.AdminInviteRequest;
import com.sellio.model.dto.request.AdminRegisterRequest;
import com.sellio.model.dto.request.AdminUpdateRequest;
import com.sellio.model.dto.request.RegisterRequest;
import com.sellio.model.dto.response.core.AdminDetailsResponse;
import com.sellio.model.dto.response.core.AdminResponse;
import com.sellio.model.dto.response.core.UserResponse;
import com.sellio.model.entity.AdminEntity;
import com.sellio.model.enums.Role;
import com.sellio.model.enums.UserStatus;
import com.sellio.model.result.*;
import com.sellio.repository.AdminRepository;
import com.sellio.repository.RefreshTokenRepository;
import com.sellio.repository.UserRepository;
import com.sellio.service.abstraction.AdminService;
import com.sellio.service.concrete.MailService;
import com.sellio.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final MailService mailService;
    private final AdminMapper adminMapper;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecurityUtil securityUtil;

    @Override
    public Result invite(AdminInviteRequest request) {
        log.info("AdminService.invite.start: {}", request);
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("AdminService.invite.exception: {}", request.getEmail());
            throw new AlreadyExistsException("Email already exists");
        }

        String key = "invite_" + request.getEmail();
        if (!redisTemplate.hasKey(key)) {
            String token = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(key, token, 24, TimeUnit.HOURS);
            mailService.sendAdminInvitationMail(request.getEmail(), token);
        }
        log.info("AdminService.invite.end: {}", request);
        return new SuccessResult("Admin invited successfully");
    }

    @Override
    @Transactional
    public DataResult<UserResponse> save(RegisterRequest registerRequest, MultipartFile logo, MultipartFile banner) {
        log.info("AdminService.save.start: {}", registerRequest);
        AdminRegisterRequest adminRegisterRequest = (AdminRegisterRequest) registerRequest;
        String key = "invite_" + registerRequest.getEmail();
        String token = adminRegisterRequest.getToken();
        if (token != null) {
            if (adminRegisterRequest.getToken().equals(redisTemplate.opsForValue().get(key))) {
                AdminEntity entity = adminMapper.registerRequestToEntity(adminRegisterRequest);
                entity.setStatus(UserStatus.ACTIVE);
                entity.setPassword(passwordEncoder.encode(adminRegisterRequest.getPassword()));
                AdminEntity savedEntity = userRepository.save(entity);
                redisTemplate.delete(key);
                mailService.sendRegistrationMail(registerRequest.getEmail());
                log.info("AdminService.save.end: {}", registerRequest);
                return new SuccessDataResult<>(adminMapper.toDetailsResponse(savedEntity), "Admin saved successfully");
            }
        }
        log.error("AdminService.save.exception: {}", registerRequest.getEmail());
        throw new InvalidInputException("Invalid token");
    }

    @Override
    public DataResult<PageData<AdminResponse>> getAllAdmins(int page, int size) {
        log.info("AdminService.getAllAdmins.start: {}", page);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminEntity> adminPage = adminRepository.findAll(pageable);

        PageData<AdminResponse> adminResponsePageData = new PageData<>(
                adminPage.getTotalPages(),
                adminPage.getTotalElements(),
                adminPage.isFirst(),
                adminPage.isLast(),
                adminPage.getSize(),
                adminPage.getNumber(),
                adminMapper.toResponses(adminPage.getContent())
        );
        log.info("AdminService.getAllAdmins.end: {}", adminResponsePageData);
        return new SuccessDataResult<>(adminResponsePageData, "Admins found successfully");
    }

    @Override
    public DataResult<AdminDetailsResponse> getAdminById(UUID id) {
        log.info("AdminService.getAdminById.start: {}", id);
        AdminEntity adminEntity = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + id));
        log.info("AdminService.getAdminById.end: {}", adminEntity);
        return new SuccessDataResult<>(adminMapper.toDetailsResponse(adminEntity), "Admin found successfully");
    }

    @Override
    @Transactional
    public DataResult<AdminDetailsResponse> updateAdminById(UUID id, AdminUpdateRequest request) {
        log.info("AdminService.updateAdminById.start: {}", id);
        AdminEntity targetEntity = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + id));
        securityUtil.validateAccess(targetEntity);

        if (!targetEntity.getUsername().equals(request.getUsername())) {
            refreshTokenRepository.deleteByUser(targetEntity);
        }

        targetEntity = adminMapper.updateRequestToEntity(request, targetEntity);
        adminRepository.save(targetEntity);
        mailService.sendUpdateMail(targetEntity.getEmail());
        log.info("AdminService.updateAdminById.end: {}", targetEntity);
        return new SuccessDataResult<>(adminMapper.toDetailsResponse(targetEntity), "Admin updated successfully. If you changed your username please login again");
    }

    @Override
    @Transactional
    public Result deleteAdminById(UUID id) {
        log.info("AdminService.deleteAdminById.start: {}", id);
        AdminEntity targetEntity = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + id));
        securityUtil.validateAccess(targetEntity);

        refreshTokenRepository.deleteByUser(targetEntity);
        adminRepository.delete(targetEntity);
        if (targetEntity.getRole() != Role.SUPER_ADMIN) {
            mailService.sendDeleteMail(targetEntity.getEmail());
        }
        log.info("AdminService.deleteAdminById.end: {}", targetEntity);
        return new SuccessResult("Admin deleted successfully");
    }
}
