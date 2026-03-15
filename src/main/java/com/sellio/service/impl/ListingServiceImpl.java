package com.sellio.service.impl;

import com.sellio.aop.annotation.CleanupCloudinary;
import com.sellio.event.ImageUploadEvent;
import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.mapper.ListingMapper;
import com.sellio.model.dto.request.ListingCreateRequest;
import com.sellio.model.dto.request.ListingUpdateRequest;
import com.sellio.model.dto.response.core.ListingDetailsResponse;
import com.sellio.model.dto.response.core.ListingResponse;
import com.sellio.model.entity.*;
import com.sellio.model.enums.DomainType;
import com.sellio.model.enums.ImageType;
import com.sellio.model.enums.ListingStatus;
import com.sellio.model.result.*;
import com.sellio.repository.*;
import com.sellio.service.abstraction.ListingService;
import com.sellio.service.concrete.CloudinaryService;
import com.sellio.service.concrete.MailService;
import com.sellio.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {
    private final UserRepository userRepository;
    private final ListingMapper listingMapper;
    private final CityRepository cityRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final ListingRepository listingRepository;
    private final FileUtil fileUtil;
    private final MailService mailService;
    private final ApplicationEventPublisher eventPublisher;
    private final PropertyValueRepository propertyValueRepository;
    private final CloudinaryService cloudinaryService;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional
    @CleanupCloudinary
    public DataResult<ListingDetailsResponse> save(ListingCreateRequest request, List<MultipartFile> images) throws IOException {
        UserEntity owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getOwnerId()));

        ListingEntity listingEntity = listingMapper.createRequestToEntity(request);
        listingEntity.setOwner(owner);

        CityEntity cityEntity = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + request.getCityId()));
        listingEntity.setCity(cityEntity);

        SubcategoryEntity subcategoryEntity = subcategoryRepository.findById(request.getSubcategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with id: " + request.getSubcategoryId()));
        listingEntity.setSubcategory(subcategoryEntity);

        List<PropertyValueEntity> selectedValues = new ArrayList<>();
        if (request.getPropertyValueIds() != null && !request.getPropertyValueIds().isEmpty()) {
            selectedValues = propertyValueRepository.findAllById(request.getPropertyValueIds());
        }

        String title;
        if (!subcategoryEntity.getIsTitleRequired()) {
            title = generateTitle(selectedValues);
        } else {
            title = request.getTitle();
        }
        listingEntity.setTitle(title);

        if (request.getPropertyValueIds() != null) {
            for (PropertyValueEntity propertyValue : selectedValues) {
                ListingPropertyEntity listingPropertyEntity = ListingPropertyEntity.builder()
                        .listing(listingEntity)
                        .value(propertyValue)
                        .build();
                listingEntity.getListingProperties().add(listingPropertyEntity);
            }
        }
        ListingEntity savedEntity = listingRepository.save(listingEntity);
        initializeImages(images, savedEntity);

        mailService.sendListingCreatedMail(owner.getEmail());
        return new SuccessDataResult<>(listingMapper.toDetailsResponse(savedEntity), "Listing created successfully");
    }

    @Override
    public DataResult<ListingDetailsResponse> getById(UUID id) {
        ListingEntity entity = listingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found with id: " + id));

        ListingDetailsResponse response = listingMapper.toDetailsResponse(entity);
        String key = "listing_view_count_" + entity.getId();
        Object viewCount = redisTemplate.opsForValue().get(key);
        if (viewCount == null) {
            redisTemplate.opsForValue().set(key, String.valueOf(1));
            viewCount = 1L;
        } else {
            long longViewCount = Long.parseLong(viewCount.toString()) + 1;
            redisTemplate.opsForValue().set(key, String.valueOf(longViewCount));
            viewCount = longViewCount;
        }
        response.setViewCount((Long) viewCount);
        return new SuccessDataResult<>(response, "Listing found successfully");
    }

    @Override
    public DataResult<PageData<ListingResponse>> getAll(UUID ownerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Page<ListingEntity> listingPage;

        if (ownerId == null) {
            listingPage = listingRepository.findAllByStatus(pageable, ListingStatus.ACTIVE);
        } else {
            userRepository.existsById(ownerId);
            listingPage = listingRepository.findAllByOwnerIdAndStatus(ownerId, pageable, ListingStatus.ACTIVE);
        }

        PageData<ListingResponse> listingResponsePageData = new PageData<>(
                listingPage.getTotalPages(),
                listingPage.getTotalElements(),
                listingPage.isFirst(),
                listingPage.isLast(),
                listingPage.getSize(),
                listingPage.getNumber(),
                listingMapper.toResponses(listingPage.getContent())
        );
        return new SuccessDataResult<>(listingResponsePageData, "Listings found successfully");
    }

    @Override
    @Transactional
    public DataResult<ListingDetailsResponse> update(UUID id, ListingUpdateRequest request, List<MultipartFile> images) throws IOException {
        ListingEntity entity = listingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found with id: " + id));
        entity = listingMapper.updateRequestToEntity(request, entity);
        if (images != null && !images.isEmpty()) {
            entity.getImages().clear();
            cloudinaryService.forceRemoveFolder("listings/" + entity.getId());
            initializeImages(images, entity);
        }
        listingRepository.save(entity);
        return new SuccessDataResult<>(listingMapper.toDetailsResponse(entity), "Listing updated successfully");
    }

    @Override
    public Result delete(UUID id) {
        ListingEntity entity = listingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found with id: " + id));
        entity.setStatus(ListingStatus.DELETED);
        entity.setDeletedAt(LocalDateTime.now());
        listingRepository.save(entity);
        return new SuccessResult("Listing added to expired list");
    }

    private String generateTitle(List<PropertyValueEntity> propertyValues) {
        if (propertyValues == null || propertyValues.isEmpty()) {
            return "New listing";
        }
        return propertyValues.stream()
                .map(PropertyValueEntity::getValue)
                .collect(Collectors.joining(" "));
    }

    private void initializeImages(List<MultipartFile> images, ListingEntity entity) throws IOException {
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile image = images.get(i);
                fileUtil.validateImage(image);

                ImageType type = (i == 0) ? ImageType.LISTING_THUMBNAIL : ImageType.LISTING;
                eventPublisher.publishEvent(
                        new ImageUploadEvent(entity.getId(), image.getBytes(), type, DomainType.LISTING)
                );
            }
        }
    }
}
