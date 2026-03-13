package com.sellio.mapper;

import com.sellio.model.dto.request.CityCreateRequest;
import com.sellio.model.dto.request.CityUpdateRequest;
import com.sellio.model.dto.response.core.CityResponse;
import com.sellio.model.entity.CityEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CityMapper {
    public CityEntity createRequestToEntity(CityCreateRequest request) {
        return CityEntity.builder()
                .name(request.getName())
                .build();
    }

    public CityResponse toResponse(CityEntity entity) {
        return CityResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public List<CityResponse> toResponses(List<CityEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    public CityEntity updateRequestToEntity(CityUpdateRequest request, CityEntity entity) {
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        return entity;
    }
}
