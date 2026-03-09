package com.sellio.service.impl;

import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.mapper.PropertyValueMapper;
import com.sellio.model.dto.request.PropertyValueAddRequest;
import com.sellio.model.dto.request.PropertyValueUpdateRequest;
import com.sellio.model.dto.response.core.PropertyValueDetailsResponse;
import com.sellio.model.dto.response.core.PropertyValueResponse;
import com.sellio.model.entity.PropertyEntity;
import com.sellio.model.entity.PropertyValueEntity;
import com.sellio.model.result.*;
import com.sellio.repository.PropertyRepository;
import com.sellio.repository.PropertyValueRepository;
import com.sellio.service.abstraction.PropertyValueService;
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
public class PropertyValueServiceImpl implements PropertyValueService {
    private final PropertyValueMapper propertyValueMapper;
    private final PropertyRepository propertyRepository;
    private final PropertyValueRepository propertyValueRepository;

    @Override
    public DataResult<PropertyValueDetailsResponse> save(PropertyValueAddRequest request) {
        PropertyEntity propertyEntity = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + request.getPropertyId()));
        PropertyValueEntity propertyValueEntity = propertyValueMapper.addRequestToEntity(request);
        propertyValueEntity.setProperty(propertyEntity);
        PropertyValueEntity savedEntity = propertyValueRepository.save(propertyValueEntity);
        return new SuccessDataResult<>(propertyValueMapper.toDetailsResponse(savedEntity), "Property value saved successfully");
    }

    @Override
    public DataResult<PropertyValueDetailsResponse> getById(UUID id) {
        PropertyValueEntity entity = propertyValueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
        return new SuccessDataResult<>(propertyValueMapper.toDetailsResponse(entity), "Property value found successfully");
    }

    @Override
    public DataResult<PageData<PropertyValueResponse>> getAll(UUID propertyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt"));
        String message;
        Page<PropertyValueEntity> propertyValuePage;

        if (propertyId == null) {
            propertyValuePage = propertyValueRepository.findAll(pageable);
            message = "Property values found successfully";
        } else {
            PropertyEntity propertyEntity = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
            propertyValuePage = propertyValueRepository.findAllByProperty(propertyEntity, pageable);
            message = "Property values found successfully for " + propertyEntity.getName();
        }

        PageData<PropertyValueResponse> propertyValueResponsePageData = new PageData<>(
                propertyValuePage.getTotalPages(),
                propertyValuePage.getTotalElements(),
                propertyValuePage.isFirst(),
                propertyValuePage.isLast(),
                propertyValuePage.getSize(),
                propertyValuePage.getNumber(),
                propertyValueMapper.toResponses(propertyValuePage.getContent())
        );
        return new SuccessDataResult<>(propertyValueResponsePageData, message);
    }

    @Override
    @Transactional
    public DataResult<PropertyValueDetailsResponse> update(UUID id, PropertyValueUpdateRequest request) {
        PropertyValueEntity propertyValueEntity = propertyValueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property value not found with id: " + id));
        if (request.getPropertyId() != null) {
            PropertyEntity propertyEntity = propertyRepository.findById(request.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + request.getPropertyId()));
            propertyValueEntity.setProperty(propertyEntity);
        }
        propertyValueEntity = propertyValueMapper.updateRequestToEntity(request, propertyValueEntity);
        return new SuccessDataResult<>(propertyValueMapper.toDetailsResponse(propertyValueEntity), "Property value updated successfully");
    }

    @Override
    public Result delete(UUID id) {
        PropertyValueEntity entity = propertyValueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
        propertyValueRepository.delete(entity);
        return new SuccessResult("Property value deleted successfully");
    }
}
