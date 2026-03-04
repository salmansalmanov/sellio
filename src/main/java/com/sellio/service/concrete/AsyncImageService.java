package com.sellio.service.concrete;

import com.sellio.event.ShopImageUploadEvent;
import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.mapper.ImageMapper;
import com.sellio.model.dto.response.core.ImageResponse;
import com.sellio.model.entity.ImageEntity;
import com.sellio.model.entity.ShopEntity;
import com.sellio.model.enums.ImageType;
import com.sellio.repository.ShopRepository;
import com.sellio.repository.UserRepository;
import com.sellio.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncImageService {
    private final ShopRepository shopRepository;
    private final FileUtil fileUtil;
    private final CloudinaryService cloudinaryService;
    private final ImageMapper imageMapper;
    private final UserRepository userRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void uploadImagesAsync(ShopImageUploadEvent event) {
        try {
            ShopEntity shopEntity = shopRepository.findById(event.getShopId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + event.getShopId()));

            if (event.getLogoBytes() != null) {
                ImageEntity logoEntity = upload(shopEntity, event.getLogoBytes(), ImageType.LOGO);
                shopEntity.setLogo(logoEntity);
            }

            if (event.getBannerBytes() != null) {
                ImageEntity bannerEntity = upload(shopEntity, event.getBannerBytes(), ImageType.BANNER);
                shopEntity.setBanner(bannerEntity);
            }
            userRepository.save(shopEntity);
            log.info("Image uploaded successfully");
        } catch (Exception ex) {
            log.error("Images failed: {}", ex.getMessage());
        }
    }

    private ImageEntity upload(ShopEntity shopEntity, byte[] fileBytes, ImageType imageType) {
        String folder = "shops/" + shopEntity.getId();
        String fileName = imageType.name();
        Map<String, Object> cloudinaryResponse = cloudinaryService.upload(fileBytes, folder, fileName);
        ImageResponse imageResponse = imageMapper.toResponse(cloudinaryResponse);
        return imageMapper.toEntity(imageResponse);
    }
}
