package com.sellio.mapper;

import com.sellio.model.dto.request.ShopRegisterRequest;
import com.sellio.model.dto.request.ShopUpdateRequest;
import com.sellio.model.dto.response.core.ShopDetailsResponse;
import com.sellio.model.dto.response.core.ShopResponse;
import com.sellio.model.entity.ShopAddressEntity;
import com.sellio.model.entity.ShopEntity;
import com.sellio.model.enums.Role;
import com.sellio.model.enums.UserStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShopMapper {
    public ShopEntity registerRequestToEntity(ShopRegisterRequest request) {
        return ShopEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .description(request.getDescription())
                .pricingPlan(request.getPricingPlan())
                .phoneNumbers(request.getPhoneNumbers())
                .role(Role.SHOP)
                .status(UserStatus.PENDING)
                .build();
    }

    public ShopDetailsResponse toDetailsResponse(ShopEntity entity) {
        ShopDetailsResponse response = new ShopDetailsResponse();

        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setEmail(entity.getEmail());
        response.setDescription(entity.getDescription());
        response.setPricingPlan(entity.getPricingPlan());
        response.setPhoneNumbers(entity.getPhoneNumbers());
        response.setRole(entity.getRole());
        response.setStatus(entity.getStatus());

        if (entity.getLogo() != null) {
            response.setLogoUrl(entity.getLogo().getSecureUrl());
        }

        if (entity.getBanner() != null) {
            response.setBannerUrl(entity.getBanner().getSecureUrl());
        }

        if (entity.getAddresses() != null) {
            for (ShopAddressEntity shopAddressEntity : entity.getAddresses()) {
                if (shopAddressEntity != null) {
                    response.getAddresses().add(shopAddressEntity.getAddress().getFullAddress());
                }
            }
        }
        return response;
    }

    public ShopResponse toResponse(ShopEntity entity) {
        ShopResponse response = new ShopResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setPhoneNumbers(entity.getPhoneNumbers());

        if (entity.getLogo() != null) {
            response.setLogoUrl(entity.getLogo().getSecureUrl());
        } else {
            response.setLogoUrl(null);
        }

        return response;
    }

    public List<ShopResponse> toResponses(List<ShopEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    public ShopEntity updateRequestToEntity(ShopUpdateRequest request, ShopEntity entity) {
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        if (request.getPhoneNumbers() != null) {
            entity.setPhoneNumbers(request.getPhoneNumbers());
        }
        return entity;
    }
}
