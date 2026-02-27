package com.sellio.mapper;

import com.sellio.model.dto.client.CloudinaryUploadResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface CloudinaryMapper {

    @Mapping(target = "publicId", expression = "java((String) json.get(\"public_id\"))")
    @Mapping(target = "secureUrl", expression = "java((String) json.get(\"secure_url\"))")
    @Mapping(target = "format", expression = "java((String) json.get(\"format\"))")
    @Mapping(target = "bytes", expression = "java((Number) json.get(\"bytes\"))")
    CloudinaryUploadResponse toCloudinaryUploadResponse(Map<String, Object> json);
}
