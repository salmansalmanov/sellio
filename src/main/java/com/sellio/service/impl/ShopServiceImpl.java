package com.sellio.service.impl;

import com.sellio.aop.annotation.CleanupCloudinary;
import com.sellio.event.ShopImageUploadEvent;
import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.mapper.ImageMapper;
import com.sellio.mapper.ShopMapper;
import com.sellio.model.dto.request.RegisterRequest;
import com.sellio.model.dto.request.ShopRegisterRequest;
import com.sellio.model.dto.request.ShopUpdateRequest;
import com.sellio.model.dto.response.core.ImageResponse;
import com.sellio.model.dto.response.core.ShopDetailsResponse;
import com.sellio.model.dto.response.core.ShopResponse;
import com.sellio.model.dto.response.core.UserResponse;
import com.sellio.model.entity.AddressEntity;
import com.sellio.model.entity.ImageEntity;
import com.sellio.model.entity.ShopAddressEntity;
import com.sellio.model.entity.ShopEntity;
import com.sellio.model.enums.ImageType;
import com.sellio.model.enums.UserStatus;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.SuccessDataResult;
import com.sellio.repository.ShopRepository;
import com.sellio.repository.UserRepository;
import com.sellio.service.abstraction.AddressService;
import com.sellio.service.abstraction.ShopService;
import com.sellio.service.concrete.AsyncImageService;
import com.sellio.service.concrete.CloudinaryService;
import com.sellio.service.concrete.MailService;
import com.sellio.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    private final ShopMapper shopMapper;
    private final CloudinaryService cloudinaryService;
    private final ImageMapper imageMapper;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final AddressService addressService;
    private final FileUtil fileUtil;
    private final ShopRepository shopRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final AsyncImageService asyncImageService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    @CleanupCloudinary
    public DataResult<UserResponse> save(RegisterRequest registerRequest, MultipartFile logo, MultipartFile banner) throws IOException {
        fileUtil.isValidImage(logo);
        fileUtil.isValidImage(banner);
        ShopRegisterRequest shopRegisterRequest = (ShopRegisterRequest) registerRequest;
        ShopEntity shopEntity = shopMapper.registerRequestToEntity(shopRegisterRequest);

        initializeAddresses(shopEntity, shopRegisterRequest.getPlaceIds());
        shopEntity.setStatus(UserStatus.ACTIVE);

        ShopEntity savedEntity = userRepository.save(shopEntity);
        mailService.sendRegistrationMail(shopEntity.getEmail());

        eventPublisher.publishEvent(
                new ShopImageUploadEvent(
                        savedEntity.getId(),
                        logo != null ? logo.getBytes() : null,
                        banner != null ? banner.getBytes() : null
                )
        );

        return new SuccessDataResult<>(shopMapper.toDetailsResponse(savedEntity), "Shop saved successfully");
    }

    @Override
    public DataResult<PageData<ShopResponse>> getAllShops(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ShopEntity> shopPage = shopRepository.findAll(pageable);

        PageData<ShopResponse> shopResponsePageData = new PageData<>(
                shopPage.getTotalPages(),
                shopPage.getTotalElements(),
                shopPage.isFirst(),
                shopPage.isLast(),
                shopPage.getSize(),
                shopPage.getNumber(),
                shopMapper.toResponses(shopPage.getContent())
        );
        for (ShopResponse shopResponse : shopResponsePageData.getContent()) {
            Object viewCount = redisTemplate.opsForValue().get(shopResponse.getId().toString());
            if (viewCount == null) {
                redisTemplate.opsForValue().set(shopResponse.getId().toString(), String.valueOf(0));
            } else {
                shopResponse.setViewCount(Long.parseLong(String.valueOf(viewCount)));
            }
        }

        return new SuccessDataResult<>(shopResponsePageData, "Shops found successfully");
    }

    @Override
    public DataResult<ShopDetailsResponse> getShopById(UUID id) {
        ShopEntity shopEntity = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + id));

        ShopDetailsResponse shopDetailsResponse = shopMapper.toDetailsResponse(shopEntity);
        Object viewCount = redisTemplate.opsForValue().get(shopEntity.getId().toString());

        if (viewCount == null) {
            redisTemplate.opsForValue().set(shopEntity.getId().toString(), String.valueOf(1));
            shopDetailsResponse.setViewCount(1L);
        } else {
            long longViewCount = Long.parseLong(viewCount.toString()) + 1;
            redisTemplate.opsForValue().set(shopEntity.getId().toString(), String.valueOf(longViewCount));
            shopDetailsResponse.setViewCount(longViewCount);
        }

        return new SuccessDataResult<>(shopDetailsResponse, "Shop found successfully");
    }

    @Override
    public DataResult<ShopDetailsResponse> updateShopById(UUID id, ShopUpdateRequest request, MultipartFile logo, MultipartFile banner) throws IOException {
        ShopEntity shopEntity = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + id));

        shopEntity = shopMapper.updateRequestToEntity(request, shopEntity);
        if (logo != null) {
            shopEntity.setLogo(initializeImage(shopEntity, logo, ImageType.LOGO));
        }
        if (banner != null) {
            shopEntity.setBanner(initializeImage(shopEntity, banner, ImageType.BANNER));
        }
        initializeAddresses(shopEntity, request.getPlaceIds());
        shopRepository.save(shopEntity);

        return new SuccessDataResult<>(shopMapper.toDetailsResponse(shopEntity), "Shop updated successfully");
    }

    @Override
    public void deleteShopById(UUID id) {
        ShopEntity entity = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + id));
        cloudinaryService.forceRemoveFolder("shops/" + entity.getName());
        shopRepository.deleteById(id);
    }

    private ImageEntity initializeImage(ShopEntity shopEntity, MultipartFile file, ImageType imageType) throws IOException {
        ImageEntity imageEntity = null;
        if (file != null) {
            if (fileUtil.isValidImage(file)) {
                String folder = "shops/" + shopEntity.getId();
                String newFileName = imageType.name();
                Map<String, Object> cloudinaryUploadResponseData = cloudinaryService.upload(file.getBytes(), folder, newFileName);
                ImageResponse cloudinaryUploadResponse = imageMapper.toResponse(cloudinaryUploadResponseData);
                imageEntity = imageMapper.toEntity(cloudinaryUploadResponse);
            }
        }
        return imageEntity;
    }

    private void initializeAddresses(ShopEntity shopEntity, Set<String> placeIds) {
        if (placeIds == null) {
            return;
        }
        shopEntity.setAddresses(new ArrayList<>());
        for (String placeId : placeIds) {
            AddressEntity addressEntity = addressService.save(placeId);

            ShopAddressEntity shopAddressEntity = new ShopAddressEntity();
            shopAddressEntity.setAddress(addressEntity);
            shopAddressEntity.setShop(shopEntity);

            shopEntity.getAddresses().add(shopAddressEntity);
        }
    }
}
