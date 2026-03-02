package com.sellio.mapper;

import com.sellio.model.dto.request.AdminRegisterRequest;
import com.sellio.model.dto.response.core.AdminDetailsResponse;
import com.sellio.model.dto.response.core.AdminResponse;
import com.sellio.model.entity.AdminEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    @Mapping(target = "role", constant = "ADMIN")
    @Mapping(target = "status", constant = "PENDING")
    AdminEntity toEntity(AdminRegisterRequest adminRegisterRequest);

    AdminDetailsResponse toDetailsResponse(AdminEntity adminEntity);

    AdminResponse toResponse(AdminEntity adminEntity);

    @AfterMapping
    default void afterMapping(AdminEntity source, @MappingTarget AdminResponse target) {
        target.setFullName(source.getFirstName() + " " + source.getLastName());
    }

    default List<AdminResponse> toResponses(List<AdminEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }
}
