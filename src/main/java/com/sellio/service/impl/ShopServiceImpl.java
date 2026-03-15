package com.sellio.service.impl;

import com.sellio.aop.annotation.CleanupCloudinary;
import com.sellio.event.ImageUploadEvent;
import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.mapper.ShopMapper;
import com.sellio.model.dto.request.RegisterRequest;
import com.sellio.model.dto.request.ShopRegisterRequest;
import com.sellio.model.dto.request.ShopUpdateRequest;
import com.sellio.model.dto.response.core.ShopDetailsResponse;
import com.sellio.model.dto.response.core.ShopResponse;
import com.sellio.model.dto.response.core.UserResponse;
import com.sellio.model.entity.AddressEntity;
import com.sellio.model.entity.ShopAddressEntity;
import com.sellio.model.entity.ShopEntity;
import com.sellio.model.enums.DomainType;
import com.sellio.model.enums.ImageType;
import com.sellio.model.enums.UserStatus;
import com.sellio.model.result.*;
import com.sellio.repository.ShopRepository;
import com.sellio.repository.UserRepository;
import com.sellio.service.abstraction.AddressService;
import com.sellio.service.abstraction.ShopService;
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
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    private final ShopMapper shopMapper;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final AddressService addressService;
    private final FileUtil fileUtil;
    private final ShopRepository shopRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    @CleanupCloudinary
    public DataResult<UserResponse> save(RegisterRequest registerRequest, MultipartFile logo, MultipartFile banner) throws IOException {
        ShopRegisterRequest shopRegisterRequest = (ShopRegisterRequest) registerRequest;
        ShopEntity shopEntity = shopMapper.registerRequestToEntity(shopRegisterRequest);

        initializeAddresses(shopEntity, shopRegisterRequest.getPlaceIds());
        shopEntity.setStatus(UserStatus.ACTIVE);

        ShopEntity savedEntity = userRepository.save(shopEntity);
        mailService.sendRegistrationMail(shopEntity.getEmail());

        fileUtil.validateImage(logo);
        eventPublisher.publishEvent(
                new ImageUploadEvent(savedEntity.getId(), logo.getBytes(), ImageType.LOGO, DomainType.SHOP)
        );

        fileUtil.validateImage(banner);
        eventPublisher.publishEvent(
                new ImageUploadEvent(savedEntity.getId(), banner.getBytes(), ImageType.BANNER, DomainType.SHOP)
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
            String key = "shop_view_count_" + shopResponse.getId();
            Object viewCount = redisTemplate.opsForValue().get(key);
            if (viewCount == null) {
                redisTemplate.opsForValue().set(key, String.valueOf(0));
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
        String key = "shop_view_count_" + shopEntity.getId();
        Object viewCount = redisTemplate.opsForValue().get(key);

        if (viewCount == null) {
            redisTemplate.opsForValue().set(key, String.valueOf(1));
            shopDetailsResponse.setViewCount(1L);
        } else {
            long longViewCount = Long.parseLong(viewCount.toString()) + 1;
            redisTemplate.opsForValue().set(key, String.valueOf(longViewCount));
            shopDetailsResponse.setViewCount(longViewCount);
        }

        return new SuccessDataResult<>(shopDetailsResponse, "Shop found successfully");
    }

    @Override
    @Transactional
    public DataResult<ShopDetailsResponse> updateShopById(UUID id, ShopUpdateRequest request, MultipartFile logo, MultipartFile banner) throws IOException {
        ShopEntity shopEntity = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + id));

        shopEntity = shopMapper.updateRequestToEntity(request, shopEntity);
        initializeAddresses(shopEntity, request.getPlaceIds());

        fileUtil.validateImage(logo);
        eventPublisher.publishEvent(
                new ImageUploadEvent(shopEntity.getId(), logo.getBytes(), ImageType.LOGO, DomainType.SHOP)
        );

        fileUtil.validateImage(banner);
        eventPublisher.publishEvent(
                new ImageUploadEvent(shopEntity.getId(), banner.getBytes(), ImageType.BANNER, DomainType.SHOP)
        );

        shopRepository.save(shopEntity);
        mailService.sendUpdateMail(shopEntity.getEmail());
        return new SuccessDataResult<>(shopMapper.toDetailsResponse(shopEntity), "Shop updated successfully");
    }

    @Override
    public Result deleteShopById(UUID id) {
        ShopEntity entity = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + id));
        cloudinaryService.forceRemoveFolder("shops/" + entity.getId());
        shopRepository.deleteById(id);
        redisTemplate.delete(String.valueOf(entity.getId()));
        mailService.sendDeleteMail(entity.getEmail());
        return new SuccessResult("Shop deleted successfully");
    }

    private void initializeAddresses(ShopEntity shopEntity, Set<String> placeIds) {
        if (placeIds == null) {
            return;
        }
        shopEntity.getAddresses().clear();
        for (String placeId : placeIds) {
            AddressEntity addressEntity = addressService.save(placeId);

            ShopAddressEntity shopAddressEntity = new ShopAddressEntity();
            shopAddressEntity.setAddress(addressEntity);
            shopAddressEntity.setShop(shopEntity);

            shopEntity.getAddresses().add(shopAddressEntity);
        }
    }
}
