package com.sellio.service.impl;

import com.sellio.aop.annotation.CleanupCloudinary;
import com.sellio.mapper.ImageMapper;
import com.sellio.mapper.ShopMapper;
import com.sellio.model.dto.domain.ImageDto;
import com.sellio.model.dto.request.RegisterRequest;
import com.sellio.model.dto.request.ShopRegisterRequest;
import com.sellio.model.dto.response.core.UserResponse;
import com.sellio.model.entity.AddressEntity;
import com.sellio.model.entity.ImageEntity;
import com.sellio.model.entity.ShopAddressEntity;
import com.sellio.model.entity.ShopEntity;
import com.sellio.model.enums.UserStatus;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.SuccessDataResult;
import com.sellio.repository.UserRepository;
import com.sellio.service.abstraction.AddressService;
import com.sellio.service.abstraction.ShopService;
import com.sellio.service.concrete.CloudinaryService;
import com.sellio.service.concrete.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    private final ShopMapper shopMapper;
    private final CloudinaryService cloudinaryService;
    private final ImageMapper imageMapper;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final AddressService addressService;

    @Override
    @Transactional
    @CleanupCloudinary
    public DataResult<UserResponse> save(RegisterRequest registerRequest, MultipartFile logo, MultipartFile banner) {
        ShopRegisterRequest shopRegisterRequest = (ShopRegisterRequest) registerRequest;
        ShopEntity shopEntity = shopMapper.toEntity(shopRegisterRequest);

        initializeImages(shopEntity, logo, banner);
        initializeAddresses(shopEntity, shopRegisterRequest.getPlaceIds());

        shopEntity.setStatus(UserStatus.ACTIVE);
        ShopEntity savedEntity = userRepository.save(shopEntity);
        mailService.sendRegistrationMail(shopEntity.getEmail());
        return new SuccessDataResult<>(shopMapper.toDetailsResponse(savedEntity), "Shop saved successfully");
    }

    private void initializeImages(ShopEntity shopEntity, MultipartFile logo, MultipartFile banner) {
        String folder = "shops/" + shopEntity.getName();

        if (logo != null) {
            Map<String, Object> logoResponseJson = cloudinaryService.upload(logo, folder);
            ImageDto logoUploadResponse = imageMapper.toCloudinaryUploadResponse(logoResponseJson);
            ImageEntity logoEntity = imageMapper.toEntity(logoUploadResponse);
            shopEntity.setLogo(logoEntity);
        }
        if (banner != null) {
            Map<String, Object> bannerResponseJson = cloudinaryService.upload(banner, folder);
            ImageDto bannerUploadResponse = imageMapper.toCloudinaryUploadResponse(bannerResponseJson);
            ImageEntity bannerEntity = imageMapper.toEntity(bannerUploadResponse);
            shopEntity.setBanner(bannerEntity);
        }
    }

    private void initializeAddresses(ShopEntity shopEntity, List<String> placeIds) {
        if (shopEntity.getAddresses() == null) {
            shopEntity.setAddresses(new ArrayList<>());
        }
        if (placeIds == null) return;

        for (String placeId : placeIds) {
            AddressEntity addressEntity = addressService.save(placeId);

            ShopAddressEntity shopAddressEntity = new ShopAddressEntity();
            shopAddressEntity.setAddress(addressEntity);
            shopAddressEntity.setShop(shopEntity);

            shopEntity.getAddresses().add(shopAddressEntity);
        }
    }
}
