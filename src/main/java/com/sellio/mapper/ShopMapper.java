package com.sellio.mapper;

import com.sellio.model.dto.request.ShopRegisterRequest;
import com.sellio.model.dto.request.ShopUpdateRequest;
import com.sellio.model.dto.response.core.ShopDetailsResponse;
import com.sellio.model.dto.response.core.ShopResponse;
import com.sellio.model.entity.ShopAddressEntity;
import com.sellio.model.entity.ShopEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ShopMapper {

    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "role", constant = "SHOP")
    ShopEntity toEntity(ShopRegisterRequest shopRegisterRequest);

    @Mapping(target = "addresses", ignore = true)
    ShopDetailsResponse toDetailsResponse(ShopEntity shopEntity);

    @AfterMapping
    default void afterMapping(ShopEntity source, @MappingTarget ShopDetailsResponse target) {
        if (source.getLogo() != null) {
            target.setLogoUrl(source.getLogo().getSecureUrl());
        }

        if (source.getBanner() != null) {
            target.setBannerUrl(source.getBanner().getSecureUrl());
        }

        if (source.getAddresses() != null) {
            target.setAddresses(new ArrayList<>());
            for (ShopAddressEntity shopAddressEntity : source.getAddresses()) {
                if (shopAddressEntity != null) {
                    target.getAddresses().add(shopAddressEntity.getAddress().getFullAddress());
                }
            }
        }
    }

    ShopResponse toResponse(ShopEntity shopEntity);

    @AfterMapping
    default void afterMapping(ShopEntity source, @MappingTarget ShopResponse target) {
        if (source.getLogo() != null) {
            target.setLogoUrl(source.getLogo().getSecureUrl());
        } else {
            target.setLogoUrl(null);
        }
    }

    default List<ShopResponse> toResponses(List<ShopEntity> shopEntities) {
        return shopEntities.stream()
                .map(this::toResponse)
                .toList();
    }

    ShopEntity updateRequestToEntity(ShopUpdateRequest request, @MappingTarget ShopEntity shopEntity);
}
