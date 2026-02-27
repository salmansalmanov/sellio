package com.sellio.mapper;

import com.sellio.model.dto.domain.ImageDto;
import com.sellio.model.entity.ImageEntity;
import com.sellio.model.enums.ImageType;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    @Mapping(target = "publicId", expression = "java((String) json.get(\"public_id\"))")
    @Mapping(target = "secureUrl", expression = "java((String) json.get(\"secure_url\"))")
    @Mapping(target = "format", expression = "java((String) json.get(\"format\"))")
    @Mapping(target = "bytes", expression = "java((Number) json.get(\"bytes\"))")
    ImageDto toCloudinaryUploadResponse(Map<String, Object> json);

    @Mapping(target = "publicId", ignore = true)
    ImageEntity toEntity(ImageDto imageDto);

    @AfterMapping
    default void afterMapping(ImageDto source, @MappingTarget ImageEntity target) {
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
