package com.sellio.service.abstraction;

import com.sellio.model.dto.client.CloudinaryUploadResponse;
import com.sellio.model.entity.ImageEntity;

public interface ImageService {
    ImageEntity save(CloudinaryUploadResponse cloudinaryUploadResponse);
}
