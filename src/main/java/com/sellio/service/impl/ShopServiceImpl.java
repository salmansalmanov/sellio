package com.sellio.service.impl;

import com.sellio.aop.annotation.CleanupCloudinary;
import com.sellio.mapper.CloudinaryMapper;
import com.sellio.mapper.ShopMapper;
import com.sellio.model.dto.client.CloudinaryUploadResponse;
import com.sellio.model.dto.request.RegisterRequest;
import com.sellio.model.dto.request.ShopRegisterRequest;
import com.sellio.model.dto.response.UserResponse;
import com.sellio.model.entity.ImageEntity;
import com.sellio.model.entity.ShopEntity;
import com.sellio.model.enums.UserStatus;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.SuccessDataResult;
import com.sellio.repository.UserRepository;
import com.sellio.service.abstraction.ImageService;
import com.sellio.service.abstraction.ShopService;
import com.sellio.service.concrete.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    private final ShopMapper shopMapper;
    private final CloudinaryService cloudinaryService;
    private final CloudinaryMapper cloudinaryMapper;
    private final ImageService imageService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    @CleanupCloudinary
    public DataResult<UserResponse> save(RegisterRequest registerRequest, MultipartFile logo, MultipartFile banner) {
        ShopRegisterRequest shopRegisterRequest = (ShopRegisterRequest) registerRequest;
        ShopEntity shopEntity = shopMapper.toEntity(shopRegisterRequest);
        initializeImages(shopEntity, logo, banner);
        shopEntity.setStatus(UserStatus.ACTIVE);
        ShopEntity savedEntity = userRepository.save(shopEntity);
        return new SuccessDataResult<>(shopMapper.toDetailsResponse(savedEntity), "Shop saved successfully");
    }

    private void initializeImages(ShopEntity shopEntity, MultipartFile logo, MultipartFile banner) {
        String folder = "shops/" + shopEntity.getName();

        if (logo != null) {
            Map<String, Object> logoResponseJson = cloudinaryService.upload(logo, folder);
            CloudinaryUploadResponse logoUploadResponse = cloudinaryMapper.toCloudinaryUploadResponse(logoResponseJson);
            ImageEntity logoEntity = imageService.save(logoUploadResponse);
            shopEntity.setLogo(logoEntity);
        }
        if (banner != null) {
            Map<String, Object> bannerResponseJson = cloudinaryService.upload(banner, folder);
            CloudinaryUploadResponse bannerUploadResponse = cloudinaryMapper.toCloudinaryUploadResponse(bannerResponseJson);
            ImageEntity bannerEntity = imageService.save(bannerUploadResponse);
            shopEntity.setBanner(bannerEntity);
        }
    }
}
