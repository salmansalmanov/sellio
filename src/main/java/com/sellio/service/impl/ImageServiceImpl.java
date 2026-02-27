package com.sellio.service.impl;

import com.sellio.mapper.ImageMapper;
import com.sellio.model.dto.client.CloudinaryUploadResponse;
import com.sellio.model.entity.ImageEntity;
import com.sellio.service.abstraction.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageMapper imageMapper;

    @Override
    public ImageEntity save(CloudinaryUploadResponse cloudinaryUploadResponse) {
        return imageMapper.toEntity(cloudinaryUploadResponse);
    }
}
