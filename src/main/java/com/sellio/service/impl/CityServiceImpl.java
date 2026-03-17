package com.sellio.service.impl;

import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.mapper.CityMapper;
import com.sellio.model.dto.request.CityCreateRequest;
import com.sellio.model.dto.request.CityUpdateRequest;
import com.sellio.model.dto.response.core.CityResponse;
import com.sellio.model.entity.CityEntity;
import com.sellio.model.result.*;
import com.sellio.repository.CityRepository;
import com.sellio.service.abstraction.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityMapper cityMapper;
    private final CityRepository cityRepository;

    @Override
    public DataResult<CityResponse> save(CityCreateRequest request) {
        log.info("CityServiceImpl.save.start: {}", request);
        CityEntity entity = cityMapper.createRequestToEntity(request);
        CityEntity savedEntity = cityRepository.save(entity);
        log.info("CityServiceImpl.save.end: {}", savedEntity);
        return new SuccessDataResult<>(cityMapper.toResponse(savedEntity), "City created successfully");
    }

    @Override
    public DataResult<CityResponse> getById(UUID id) {
        log.info("CityServiceImpl.getById.start: {}", id);
        CityEntity entity = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id " + id));
        log.info("CityServiceImpl.getById.end: {}", entity);
        return new SuccessDataResult<>(cityMapper.toResponse(entity), "City found successfully");
    }

    @Override
    public DataResult<PageData<CityResponse>> getAll(int page, int size) {
        log.info("CityServiceImpl.getAll.start: {}", page);
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").descending());
        Page<CityEntity> cityPage = cityRepository.findAll(pageable);
        PageData<CityResponse> cityResponsePageData = new PageData<>(
                cityPage.getTotalPages(),
                cityPage.getTotalElements(),
                cityPage.isFirst(),
                cityPage.isLast(),
                cityPage.getSize(),
                cityPage.getNumber(),
                cityMapper.toResponses(cityPage.getContent())
        );
        log.info("CityServiceImpl.getAll.end: {}", cityResponsePageData);
        return new SuccessDataResult<>(cityResponsePageData, "Cities found successfully");
    }

    @Override
    @Transactional
    public DataResult<CityResponse> update(UUID id, CityUpdateRequest request) {
        log.info("CityServiceImpl.update.start: {}", id);
        CityEntity entity = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id " + id));
        entity = cityMapper.updateRequestToEntity(request, entity);
        log.info("CityServiceImpl.update.end: {}", entity);
        return new SuccessDataResult<>(cityMapper.toResponse(entity), "City updated successfully");
    }

    @Override
    public Result delete(UUID id) {
        log.info("CityServiceImpl.deleteById.start: {}", id);
        CityEntity entity = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id " + id));
        cityRepository.delete(entity);
        log.info("CityServiceImpl.deleteById.end: {}", entity);
        return new SuccessResult("City deleted successfully");
    }
}
