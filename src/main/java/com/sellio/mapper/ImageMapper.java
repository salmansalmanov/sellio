package com.sellio.mapper;

import com.sellio.model.dto.client.CloudinaryUploadResponse;
import com.sellio.model.entity.ImageEntity;
import com.sellio.model.enums.ImageType;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    @Mapping(target = "publicId", ignore = true)
    ImageEntity toEntity(CloudinaryUploadResponse cloudinaryUploadResponse);

    @AfterMapping
    default void afterMapping(CloudinaryUploadResponse source, @MappingTarget ImageEntity target) {
        if (source.getPublicId() != null) {
            String publicId = source.getPublicId();
            target.setPublicId(publicId);

            String fileName = publicId.substring(publicId.lastIndexOf('/') + 1);
            target.setFileName(fileName);

            target.setType(ImageType.LOGO);
        }

        if (source.getBytes() != null) {
            target.setSize(source.getBytes().longValue());
        } else {
            target.setSize(0L);
        }
    }
}
