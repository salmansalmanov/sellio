package com.sellio.mapper;

import com.sellio.model.dto.request.AdminRegisterRequest;
import com.sellio.model.dto.response.core.AdminDetailsResponse;
import com.sellio.model.entity.AdminEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    @Mapping(target = "role", constant = "ADMIN")
    @Mapping(target = "status", constant = "PENDING")
    AdminEntity toEntity(AdminRegisterRequest adminRegisterRequest);

    AdminDetailsResponse toDetailsResponse(AdminEntity adminEntity);
}
