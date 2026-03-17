package com.sellio.service.impl;

import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.mapper.CategoryMapper;
import com.sellio.model.dto.request.CategoryCreateRequest;
import com.sellio.model.dto.request.CategoryUpdateRequest;
import com.sellio.model.dto.response.core.CategoryDetailsResponse;
import com.sellio.model.dto.response.core.CategoryResponse;
import com.sellio.model.entity.CategoryEntity;
import com.sellio.model.result.*;
import com.sellio.repository.CategoryRepository;
import com.sellio.service.abstraction.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public DataResult<CategoryDetailsResponse> save(CategoryCreateRequest request) {
        log.info("CategoryServiceImpl.save.start: {}", request);
        CategoryEntity entity = categoryMapper.createRequestToEntity(request);
        CategoryEntity savedEntity = categoryRepository.save(entity);
        log.info("CategoryServiceImpl.save.end: {}", savedEntity);
        return new SuccessDataResult<>(categoryMapper.toDetailsResponse(savedEntity), "Category created successfully");
    }

    @Override
    public DataResult<PageData<CategoryResponse>> getAll(int page, int size) {
        log.info("CategoryServiceImpl.getAll.start: {}", page);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt"));
        Page<CategoryEntity> categoryPage = categoryRepository.findAll(pageable);
        PageData<CategoryResponse> categoryResponsePageData = new PageData<>(
                categoryPage.getTotalPages(),
                categoryPage.getTotalElements(),
                categoryPage.isFirst(),
                categoryPage.isLast(),
                categoryPage.getSize(),
                categoryPage.getNumber(),
                categoryMapper.toResponses(categoryPage.getContent())
        );
        log.info("CategoryServiceImpl.getAll.end: {}", categoryResponsePageData);
        return new SuccessDataResult<>(categoryResponsePageData, "Categories found successfully");
    }

    @Override
    public DataResult<CategoryDetailsResponse> getById(UUID id) {
        log.info("CategoryServiceImpl.getById.start: {}", id);
        CategoryEntity categoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
        log.info("CategoryServiceImpl.getById.end: {}", categoryEntity);
        return new SuccessDataResult<>(categoryMapper.toDetailsResponse(categoryEntity), "Category found successfully");
    }

    @Override
    public DataResult<CategoryDetailsResponse> updateById(UUID id, CategoryUpdateRequest request) {
        log.info("CategoryServiceImpl.updateById.start: {}", id);
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
        entity = categoryMapper.updateRequestToEntity(request, entity);
        CategoryEntity savedEntity = categoryRepository.save(entity);
        log.info("CategoryServiceImpl.updateById.end: {}", savedEntity);
        return new SuccessDataResult<>(categoryMapper.toDetailsResponse(savedEntity), "Category updated successfully");
    }

    @Override
    public Result deleteById(UUID id) {
        log.info("CategoryServiceImpl.deleteById.start: {}", id);
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
        categoryRepository.delete(entity);
        log.info("CategoryServiceImpl.deleteById.end: {}", entity);
        return new SuccessResult("Category deleted successfully");
    }
}
