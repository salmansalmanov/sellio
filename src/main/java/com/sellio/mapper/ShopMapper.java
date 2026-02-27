package com.sellio.mapper;

import com.sellio.model.dto.request.ShopRegisterRequest;
import com.sellio.model.dto.response.ShopDetailsResponse;
import com.sellio.model.entity.ShopEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ShopMapper {

    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "role", constant = "SHOP")
    ShopEntity toEntity(ShopRegisterRequest shopRegisterRequest);

    ShopDetailsResponse toDetailsResponse(ShopEntity shopEntity);

    @AfterMapping
    default void afterMapping(ShopEntity source, @MappingTarget ShopDetailsResponse target) {
        if (source.getLogo() != null) {
            target.setLogoUrl(source.getLogo().getSecureUrl());
        }

        if (source.getBanner() != null) {
            target.setBannerUrl(source.getBanner().getSecureUrl());
        }
    }
}
