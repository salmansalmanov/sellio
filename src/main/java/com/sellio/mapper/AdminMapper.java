package com.sellio.mapper;

import com.sellio.model.dto.request.AdminRegisterRequest;
import com.sellio.model.dto.request.AdminUpdateRequest;
import com.sellio.model.dto.response.core.AdminDetailsResponse;
import com.sellio.model.dto.response.core.AdminResponse;
import com.sellio.model.entity.AdminEntity;
import com.sellio.model.enums.Role;
import com.sellio.model.enums.UserStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminMapper {
    public AdminEntity registerRequestToEntity(AdminRegisterRequest request) {
        return AdminEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.ADMIN)
                .status(UserStatus.PENDING)
                .build();
    }

    public AdminDetailsResponse toDetailsResponse(AdminEntity entity) {
        return AdminDetailsResponse.builder()
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .username(entity.getUsername())
                .status(entity.getStatus())
                .role(entity.getRole())
                .build();
    }

    public AdminResponse toResponse(AdminEntity entity) {
        return AdminResponse.builder()
                .id(entity.getId())
                .fullName(entity.getFirstName() + " " + entity.getLastName())
                .username(entity.getUsername())
                .status(entity.getStatus())
                .build();
    }

    public List<AdminResponse> toResponses(List<AdminEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    public AdminEntity updateRequestToEntity(AdminUpdateRequest request, AdminEntity entity) {
        if (request.getFirstName() != null) {
            entity.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            entity.setLastName(request.getLastName());
        }
        if (request.getUsername() != null) {
            entity.setUsername(request.getUsername());
        }
        return entity;
    }
}
