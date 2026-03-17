package com.sellio.service.impl;

import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.mapper.PropertyDependencyMapper;
import com.sellio.mapper.PropertyValueMapper;
import com.sellio.model.dto.request.PropertyDependencyCreateRequest;
import com.sellio.model.dto.response.core.PropertyDependencyDetailsResponse;
import com.sellio.model.dto.response.core.PropertyDependencyResponse;
import com.sellio.model.entity.PropertyDependencyEntity;
import com.sellio.model.entity.PropertyValueEntity;
import com.sellio.model.result.*;
import com.sellio.repository.PropertyDependencyRepository;
import com.sellio.repository.PropertyValueRepository;
import com.sellio.service.abstraction.PropertyDependencyService;
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
public class PropertyDependencyServiceImpl implements PropertyDependencyService {
    private final PropertyValueRepository propertyValueRepository;
    private final PropertyDependencyRepository propertyDependencyRepository;
    private final PropertyValueMapper propertyValueMapper;
    private final PropertyDependencyMapper propertyDependencyMapper;

    @Override
    @Transactional
    public DataResult<PropertyDependencyDetailsResponse> save(PropertyDependencyCreateRequest request) {
        log.info("PropertyDependencyServiceImpl.save.start: {}", request);
        PropertyValueEntity parentPropertyValueEntity = propertyValueRepository.findById(request.getParentPropertyValueId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent property value not found with id: " + request.getParentPropertyValueId()));

        List<PropertyValueEntity> childPropertyValues = propertyValueRepository.findAllById(request.getChildPropertyValueIds());

        PropertyDependencyDetailsResponse response = PropertyDependencyDetailsResponse.builder()
                .parentPropertyValueId(parentPropertyValueEntity.getId())
                .parentPropertyValue(parentPropertyValueEntity.getValue())
                .childPropertyValues(new ArrayList<>())
                .build();

        for (PropertyValueEntity childPropertyValue : childPropertyValues) {
            if (parentPropertyValueEntity.getId().equals(childPropertyValue.getId())) {
                continue;
            }

            boolean exists = propertyDependencyRepository.existsByParentIdAndChildId(parentPropertyValueEntity.getId(), childPropertyValue.getId());

            if (!exists) {
                PropertyDependencyEntity propertyDependencyEntity = PropertyDependencyEntity.builder()
                        .parent(parentPropertyValueEntity)
                        .child(childPropertyValue)
                        .build();

                propertyDependencyRepository.save(propertyDependencyEntity);

                response.getChildPropertyValues().add(propertyValueMapper.toResponse(childPropertyValue));
            }
        }
        log.info("PropertyDependencyServiceImpl.save.end: {}", response);
        return new SuccessDataResult<>(response, "Property dependency created successfully");
    }

    @Override
    public DataResult<PropertyDependencyResponse> getById(UUID id) {
        log.info("PropertyDependencyServiceImpl.getById.start: {}", id);
        PropertyDependencyEntity entity = propertyDependencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PropertyDependency not found with id: " + id));
        log.info("PropertyDependencyServiceImpl.getById.end: {}", entity);
        return new SuccessDataResult<>(propertyDependencyMapper.toResponse(entity), "Property dependency found successfully");
    }

    @Override
    public DataResult<PageData<PropertyDependencyResponse>> getAll(UUID parentPropertyValueId, int page, int size) {
        log.info("PropertyDependencyServiceImpl.getAll.start: {}", parentPropertyValueId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt"));
        String message;
        Page<PropertyDependencyEntity> propertyDependencyPage;

        if (parentPropertyValueId == null) {
            propertyDependencyPage = propertyDependencyRepository.findAll(pageable);
            message = "Property dependencies found successfully";
        } else {
            PropertyValueEntity parentPropertyValueEntity = propertyValueRepository.findById(parentPropertyValueId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent property value not found with id: " + parentPropertyValueId));
            propertyDependencyPage = propertyDependencyRepository.findAllByParent(parentPropertyValueEntity, pageable);
            message = "Property dependencies found successfully for " + parentPropertyValueEntity.getValue();
        }

        PageData<PropertyDependencyResponse> propertyDependencyResponsePageData = new PageData<>(
                propertyDependencyPage.getTotalPages(),
                propertyDependencyPage.getTotalElements(),
                propertyDependencyPage.isFirst(),
                propertyDependencyPage.isLast(),
                propertyDependencyPage.getSize(),
                propertyDependencyPage.getNumber(),
                propertyDependencyMapper.toResponses(propertyDependencyPage.getContent())
        );
        log.info("PropertyDependencyServiceImpl.getAll.end: {}", propertyDependencyResponsePageData);
        return new SuccessDataResult<>(propertyDependencyResponsePageData, message);
    }

    @Override
    public Result deleteById(UUID id) {
        log.info("PropertyDependencyServiceImpl.deleteById.start: {}", id);
        PropertyDependencyEntity entity = propertyDependencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PropertyDependency not found with id: " + id));
        propertyDependencyRepository.delete(entity);
        log.info("PropertyDependencyServiceImpl.deleteById.end: {}", entity);
        return new SuccessResult("Property dependency deleted successfully");
    }
}
