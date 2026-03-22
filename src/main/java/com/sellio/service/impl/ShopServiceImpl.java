package com.sellio.service.impl;

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
import com.sellio.model.entity.ListingEntity;
import com.sellio.model.entity.ShopAddressEntity;
import com.sellio.model.entity.ShopEntity;
import com.sellio.model.enums.DomainType;
import com.sellio.model.enums.ImageType;
import com.sellio.model.enums.UserStatus;
import com.sellio.model.result.*;
import com.sellio.repository.RefreshTokenRepository;
import com.sellio.repository.ShopRepository;
import com.sellio.repository.UserRepository;
import com.sellio.service.abstraction.AddressService;
import com.sellio.service.abstraction.ShopService;
import com.sellio.service.concrete.CloudinaryService;
import com.sellio.service.concrete.MailService;
import com.sellio.util.FileUtil;
import com.sellio.util.RedisUtil;
import com.sellio.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Slf4j
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
    private final RedisUtil redisUtil;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecurityUtil securityUtil;

    @Override
    public DataResult<UserResponse> save(RegisterRequest registerRequest, MultipartFile logo, MultipartFile banner) throws IOException {
        log.info("ShopServiceImpl.save.start: {}", registerRequest);
        ShopRegisterRequest shopRegisterRequest = (ShopRegisterRequest) registerRequest;
        ShopEntity shopEntity = shopMapper.registerRequestToEntity(shopRegisterRequest);

        initializeAddresses(shopEntity, shopRegisterRequest.getPlaceIds());
        shopEntity.setStatus(UserStatus.ACTIVE);
        shopEntity.setPassword(passwordEncoder.encode(shopRegisterRequest.getPassword()));

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
        String viewCount = "shop_view_count_" + savedEntity.getId();
        redisTemplate.opsForValue().set(viewCount, String.valueOf(0));

        String listingCount = "listing_count_" + savedEntity.getId();
        redisTemplate.opsForValue().set(listingCount, "0");

        log.info("ShopServiceImpl.save.end: {}", savedEntity);
        return new SuccessDataResult<>(shopMapper.toDetailsResponse(savedEntity), "Shop saved successfully");
    }

    @Override
    public DataResult<PageData<ShopResponse>> getAllShops(int page, int size) {
        log.info("ShopServiceImpl.getAllShops.start: {}", page);
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
            shopResponse.setViewCount(redisUtil.getViewCount(shopResponse.getId(), DomainType.SHOP));
        }

        log.info("ShopServiceImpl.getAllShops.end: {}", shopResponsePageData);
        return new SuccessDataResult<>(shopResponsePageData, "Shops found successfully");
    }

    @Override
    public DataResult<ShopDetailsResponse> getShopById(UUID id) {
        log.info("ShopServiceImpl.getShopById.start: {}", id);
        ShopEntity shopEntity = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + id));

        ShopDetailsResponse shopDetailsResponse = shopMapper.toDetailsResponse(shopEntity);
        shopDetailsResponse.setViewCount(redisUtil.initializeViewCount(id, DomainType.SHOP));
        log.info("ShopServiceImpl.getShopById.end: {}", shopDetailsResponse);
        return new SuccessDataResult<>(shopDetailsResponse, "Shop found successfully");
    }

    @Override
    public DataResult<ShopDetailsResponse> updateShopById(UUID id, ShopUpdateRequest request, MultipartFile logo, MultipartFile banner) throws IOException {
        log.info("ShopServiceImpl.updateShopById.start: {}", id);
        ShopEntity targetEntity = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + id));

        securityUtil.validateAccess(targetEntity);
        targetEntity = shopMapper.updateRequestToEntity(request, targetEntity);
        initializeAddresses(targetEntity, request.getPlaceIds());

        fileUtil.validateImage(logo);
        eventPublisher.publishEvent(
                new ImageUploadEvent(targetEntity.getId(), logo.getBytes(), ImageType.LOGO, DomainType.SHOP)
        );

        fileUtil.validateImage(banner);
        eventPublisher.publishEvent(
                new ImageUploadEvent(targetEntity.getId(), banner.getBytes(), ImageType.BANNER, DomainType.SHOP)
        );

        shopRepository.save(targetEntity);
        mailService.sendUpdateMail(targetEntity.getEmail());
        log.info("ShopServiceImpl.updateShopById.end: {}", targetEntity);
        return new SuccessDataResult<>(shopMapper.toDetailsResponse(targetEntity), "Shop updated successfully");
    }

    @Override
    @Transactional
    public Result deleteShopById(UUID id) {
        log.info("ShopServiceImpl.deleteShopById.start: {}", id);
        ShopEntity targetEntity = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + id));

        securityUtil.validateAccess(targetEntity);
        refreshTokenRepository.deleteByUser(targetEntity);
        cloudinaryService.forceRemoveFolder("shops/" + targetEntity.getId());
        for (ListingEntity listingEntity : targetEntity.getListings()) {
            redisTemplate.delete("listing_view_count_" + listingEntity.getId());
            redisTemplate.delete("listing_count_" + listingEntity.getId());
        }
        shopRepository.deleteById(id);
        redisTemplate.delete(String.valueOf(targetEntity.getId()));
        mailService.sendDeleteMail(targetEntity.getEmail());
        log.info("ShopServiceImpl.deleteShopById.end: {}", targetEntity);
        return new SuccessResult("Shop deleted successfully");
    }

    private void initializeAddresses(ShopEntity shopEntity, Set<String> placeIds) {
        log.info("ShopServiceImpl.initializeAddresses.start: {}", shopEntity);
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
        log.info("ShopServiceImpl.initializeAddresses.end: {}", shopEntity);
    }
}
