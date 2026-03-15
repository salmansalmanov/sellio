package com.sellio.service.impl;

import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.mapper.PropertyMapper;
import com.sellio.model.dto.request.PropertyCreateRequest;
import com.sellio.model.dto.request.PropertyUpdateRequest;
import com.sellio.model.dto.response.core.PropertyDetailsResponse;
import com.sellio.model.dto.response.core.PropertyResponse;
import com.sellio.model.entity.PropertyEntity;
import com.sellio.model.entity.SubcategoryEntity;
import com.sellio.model.result.*;
import com.sellio.repository.PropertyRepository;
import com.sellio.repository.SubcategoryRepository;
import com.sellio.service.abstraction.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {
    private final SubcategoryRepository subcategoryRepository;
    private final PropertyMapper propertyMapper;
    private final PropertyRepository propertyRepository;

    @Override
    public DataResult<PropertyDetailsResponse> save(PropertyCreateRequest request) {
        SubcategoryEntity subcategoryEntity = subcategoryRepository.findById(request.getSubcategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with id: " + request.getSubcategoryId()));
        PropertyEntity propertyEntity = propertyMapper.createRequestToEntity(request);
        propertyEntity.setSubcategory(subcategoryEntity);
        PropertyEntity savedEntity = propertyRepository.save(propertyEntity);
        return new SuccessDataResult<>(propertyMapper.toDetailsResponse(savedEntity), "Property created successfully");
    }

    @Override
    public DataResult<PropertyDetailsResponse> getById(UUID id) {
        PropertyEntity propertyEntity = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
        return new SuccessDataResult<>(propertyMapper.toDetailsResponse(propertyEntity), "Property created successfully");
    }

    @Override
    public DataResult<PageData<PropertyResponse>> getAll(UUID subcategoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt"));
        String message;
        Page<PropertyEntity> propertyPage;

        if (subcategoryId == null) {
            propertyPage = propertyRepository.findAll(pageable);
            message = "Properties found successfully";
        } else {
            SubcategoryEntity subcategoryEntity = subcategoryRepository.findById(subcategoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with id: " + subcategoryId));
            propertyPage = propertyRepository.findAllBySubcategory(subcategoryEntity, pageable);
            message = "Subcategory found successfully for " + subcategoryEntity.getName();
        }

        PageData<PropertyResponse> propertyResponsePageData = new PageData<>(
                propertyPage.getTotalPages(),
                propertyPage.getTotalElements(),
                propertyPage.isFirst(),
                propertyPage.isLast(),
                propertyPage.getSize(),
                propertyPage.getNumber(),
                propertyMapper.toResponses(propertyPage.getContent())
        );
        return new SuccessDataResult<>(propertyResponsePageData, message);
    }

    @Override
    @Transactional
    public DataResult<PropertyDetailsResponse> update(UUID id, PropertyUpdateRequest request) {
        PropertyEntity propertyEntity = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
        if (request.getSubcategoryId() != null) {
            SubcategoryEntity subcategoryEntity = subcategoryRepository.findById(request.getSubcategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with id: " + request.getSubcategoryId()));
            propertyEntity.setSubcategory(subcategoryEntity);
        }

        propertyMapper.updateRequestToEntity(request, propertyEntity);
        return new SuccessDataResult<>(propertyMapper.toDetailsResponse(propertyEntity), "Property updated successfully");
    }

    @Override
    public Result delete(UUID id) {
        PropertyEntity entity = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
        propertyRepository.delete(entity);
        return new SuccessResult("Property deleted successfully");
    }
}
