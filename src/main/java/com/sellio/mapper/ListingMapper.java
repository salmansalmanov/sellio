package com.sellio.mapper;

import com.sellio.model.dto.request.ListingCreateRequest;
import com.sellio.model.dto.request.ListingUpdateRequest;
import com.sellio.model.dto.response.core.ListingDetailsResponse;
import com.sellio.model.dto.response.core.ListingResponse;
import com.sellio.model.entity.*;
import com.sellio.model.enums.Role;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ListingMapper {
    private final CityMapper cityMapper;
    private final ShopMapper shopMapper;
    private final CustomerMapper customerMapper;
    private final PropertyValueMapper propertyValueMapper;
    private final CategoryMapper categoryMapper;
    private final SubcategoryMapper subcategoryMapper;

    public ListingEntity createRequestToEntity(ListingCreateRequest request) {
        return ListingEntity.builder()
                .description(request.getDescription())
                .price(request.getPrice())
                .isNew(request.getIsNew())
                .hasDelivery(request.getHasDelivery())
                .listingProperties(new ArrayList<>())
                .build();
    }

    public ListingDetailsResponse toDetailsResponse(ListingEntity entity) {
        ListingDetailsResponse response = ListingDetailsResponse.builder()
                .title(entity.getTitle())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .status(entity.getStatus())
                .city(cityMapper.toResponse(entity.getCity()))
                .category(categoryMapper.toResponse(entity.getSubcategory().getCategory()))
                .subcategory(subcategoryMapper.toResponse(entity.getSubcategory()))
                .isNew(entity.isNew())
                .hasDelivery(entity.isHasDelivery())
                .id(entity.getId())
                .updatedAt(entity.getUpdatedAt())
                .build();

        if (entity.getOwner().getRole() == Role.SHOP) {
            ShopEntity shop = (ShopEntity) Hibernate.unproxy(entity.getOwner());
            response.setOwner(shopMapper.toDetailsResponse(shop));
        } else if (entity.getOwner().getRole() == Role.CUSTOMER) {
            CustomerEntity customer = (CustomerEntity) Hibernate.unproxy(entity.getOwner());
            response.setOwner(customerMapper.toDetailsResponse(customer));
        }

        response.setPropertyValues(new ArrayList<>());
        for (ListingPropertyEntity listingProperty : entity.getListingProperties()) {
            response.getPropertyValues().add(propertyValueMapper.toResponse(listingProperty.getValue()));
        }

        response.setImageUrls(new ArrayList<>());
        for (ImageEntity image : entity.getImages()) {
            response.getImageUrls().add(image.getSecureUrl());
        }
        return response;
    }

    public ListingResponse toResponse(ListingEntity entity) {
        return ListingResponse.builder()
                .id(entity.getId())
                .thumbnailUrl(entity.getThumbnail().getSecureUrl())
                .price(entity.getPrice())
                .title(entity.getTitle())
                .city(cityMapper.toResponse(entity.getCity()))
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<ListingResponse> toResponses(List<ListingEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    public ListingEntity updateRequestToEntity(ListingUpdateRequest request, ListingEntity entity) {
        if (request.getHasDelivery() != null) {
            entity.setHasDelivery(request.getHasDelivery());
        }
        if (request.getPrice() != null) {
            entity.setPrice(request.getPrice());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        return entity;
    }
}
