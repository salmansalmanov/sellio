package com.sellio.service.concrete;

import com.sellio.event.ImageUploadEvent;
import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.mapper.ImageMapper;
import com.sellio.model.dto.response.core.ImageResponse;
import com.sellio.model.entity.ImageEntity;
import com.sellio.model.entity.ListingEntity;
import com.sellio.model.enums.DomainType;
import com.sellio.model.enums.ImageType;
import com.sellio.repository.ImageRepository;
import com.sellio.repository.ListingRepository;
import com.sellio.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncImageService {
    private final ShopRepository shopRepository;
    private final CloudinaryService cloudinaryService;
    private final ImageMapper imageMapper;
    private final ImageRepository imageRepository;
    private final ListingRepository listingRepository;

    @Async("imageUploadExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void uploadImagesAsync(ImageUploadEvent event) {
        try {
            ImageEntity imageEntity = upload(event.getReferenceId(), event.getFileBytes(), event.getImageType(), event.getDomainType());
            ImageEntity savedImage = imageRepository.save(imageEntity);

            updateDomainEntity(event, savedImage);

            log.info("Image uploaded successfully for domain {}", event.getDomainType());
        } catch (Exception e) {
            log.error("Error uploading image for domain {}: {}", event.getDomainType(), e.getMessage());
        }
    }

    private void updateDomainEntity(ImageUploadEvent event, ImageEntity image) {
        switch (event.getDomainType()) {
            case SHOP -> {
                if (event.getImageType() == ImageType.LOGO) {
                    shopRepository.updateLogo(event.getReferenceId(), image);
                } else if (event.getImageType() == ImageType.BANNER) {
                    shopRepository.updateBanner(event.getReferenceId(), image);
                }
            }
            case LISTING -> {
                ListingEntity listingEntity = listingRepository.findById(event.getReferenceId())
                        .orElseThrow(() -> new ResourceNotFoundException("Listing not found with ID: " + event.getReferenceId()));
                image.setListing(listingEntity);
                if (event.getImageType() == ImageType.LISTING_THUMBNAIL) {
                    listingEntity.setThumbnail(image);
                    listingRepository.save(listingEntity);
                }
                imageRepository.save(image);
            }
        }
    }

    private ImageEntity upload(UUID referenceId, byte[] bytes, ImageType imageType, DomainType domainType) {
        String folder = domainType.name().toLowerCase() + "s/" + referenceId;

        String fileName = (imageType == ImageType.LISTING || imageType == ImageType.LISTING_THUMBNAIL) ? String.format("listing_%s", UUID.randomUUID())
                : imageType.name();

        Map<String, Object> cloudinaryResponse = cloudinaryService.upload(bytes, folder, fileName);
        ImageResponse imageResponse = imageMapper.toResponse(cloudinaryResponse);
        return imageMapper.toEntity(imageResponse, imageType);
    }
}
