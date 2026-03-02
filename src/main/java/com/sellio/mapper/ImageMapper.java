package com.sellio.mapper;

import com.sellio.model.dto.response.core.ImageResponse;
import com.sellio.model.entity.ImageEntity;
import com.sellio.model.enums.ImageType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ImageMapper {
    public ImageResponse toResponse(Map<String, Object> data) {
        return ImageResponse.builder()
                .publicId((String) data.get("public_id"))
                .secureUrl((String) data.get("secure_url"))
                .format((String) data.get("format"))
                .bytes((Number) data.get("bytes"))
                .build();
    }

    public ImageEntity toEntity(ImageResponse response) {
        return ImageEntity.builder()
                .fileName(response.getPublicId().substring(response.getPublicId().lastIndexOf('/') + 1))
                .publicId(response.getPublicId())
                .secureUrl(response.getSecureUrl())
                .format(response.getFormat())
                .size(response.getBytes().longValue())
                .type(ImageType.LOGO)
                .build();
    }
}
