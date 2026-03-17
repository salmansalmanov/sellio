package com.sellio.service.impl;

import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.mapper.PropertyValueMapper;
import com.sellio.model.dto.request.PropertyValueAddRequest;
import com.sellio.model.dto.request.PropertyValueUpdateRequest;
import com.sellio.model.dto.response.core.PropertyValueGetResponse;
import com.sellio.model.dto.response.core.PropertyValueResponse;
import com.sellio.model.dto.response.core.PropertyValueSaveResponse;
import com.sellio.model.entity.PropertyEntity;
import com.sellio.model.entity.PropertyValueEntity;
import com.sellio.model.result.*;
import com.sellio.repository.PropertyRepository;
import com.sellio.repository.PropertyValueRepository;
import com.sellio.service.abstraction.PropertyValueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyValueServiceImpl implements PropertyValueService {
    private final PropertyValueMapper propertyValueMapper;
    private final PropertyRepository propertyRepository;
    private final PropertyValueRepository propertyValueRepository;

    @Override
    @Transactional
    public DataResult<PropertyValueSaveResponse> save(PropertyValueAddRequest request) {
        log.info("PropertyValueServiceImpl.save.start: {}", request);
        PropertyEntity propertyEntity = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + request.getPropertyId()));

        List<PropertyValueEntity> propertyValueEntities = new ArrayList<>();
        for (String value : request.getValues()) {
            PropertyValueEntity propertyValueEntity = PropertyValueEntity.builder()
                    .property(propertyEntity)
                    .value(value)
                    .build();
            propertyValueEntities.add(propertyValueEntity);
        }

        List<PropertyValueEntity> savedEntities = propertyValueRepository.saveAll(propertyValueEntities);
        log.info("PropertyValueServiceImpl.save.end: {}", savedEntities);
        return new SuccessDataResult<>(propertyValueMapper.toSaveResponse(savedEntities), "Property value saved successfully");
    }

    @Override
    public DataResult<PropertyValueGetResponse> getById(UUID id) {
        log.info("PropertyValueServiceImpl.getById.start: {}", id);
        PropertyValueEntity entity = propertyValueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
        log.info("PropertyValueServiceImpl.getById.end: {}", entity);
        return new SuccessDataResult<>(propertyValueMapper.toGetResponse(entity), "Property value found successfully");
    }

    @Override
    public DataResult<PageData<PropertyValueResponse>> getAll(UUID propertyId, int page, int size) {
        log.info("PropertyValueServiceImpl.getAll.start: {}", propertyId);
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
        log.info("PropertyValueServiceImpl.getAll.end: {}", propertyValueResponsePageData);
        return new SuccessDataResult<>(propertyValueResponsePageData, message);
    }

    @Override
    @Transactional
    public DataResult<PropertyValueGetResponse> update(UUID id, PropertyValueUpdateRequest request) {
        log.info("PropertyValueServiceImpl.update.start: {}", id);
        PropertyValueEntity propertyValueEntity = propertyValueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property value not found with id: " + id));
        if (request.getPropertyId() != null) {
            PropertyEntity propertyEntity = propertyRepository.findById(request.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + request.getPropertyId()));
            propertyValueEntity.setProperty(propertyEntity);
        }
        propertyValueEntity = propertyValueMapper.updateRequestToEntity(request, propertyValueEntity);
        log.info("PropertyValueServiceImpl.update.end: {}", propertyValueEntity);
        return new SuccessDataResult<>(propertyValueMapper.toGetResponse(propertyValueEntity), "Property value updated successfully");
    }

    @Override
    public Result delete(UUID id) {
        log.info("PropertyValueServiceImpl.delete.start: {}", id);
        PropertyValueEntity entity = propertyValueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
        propertyValueRepository.delete(entity);
        log.info("PropertyValueServiceImpl.delete.end: {}", entity);
        return new SuccessResult("Property value deleted successfully");
    }
}
