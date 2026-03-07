package com.sellio.service.impl;

import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.mapper.SubcategoryMapper;
import com.sellio.model.dto.request.SubcategoryCreateRequest;
import com.sellio.model.dto.request.SubcategoryUpdateRequest;
import com.sellio.model.dto.response.core.SubcategoryDetailsResponse;
import com.sellio.model.dto.response.core.SubcategoryResponse;
import com.sellio.model.entity.CategoryEntity;
import com.sellio.model.entity.SubcategoryEntity;
import com.sellio.model.result.*;
import com.sellio.repository.CategoryRepository;
import com.sellio.repository.SubcategoryRepository;
import com.sellio.service.abstraction.SubcategoryService;
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
public class SubcategoryServiceImpl implements SubcategoryService {
    private final CategoryRepository categoryRepository;
    private final SubcategoryMapper subcategoryMapper;
    private final SubcategoryRepository subcategoryRepository;

    @Override
    @Transactional
    public DataResult<SubcategoryDetailsResponse> save(SubcategoryCreateRequest request) {
        CategoryEntity categoryEntity = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id " + request.getCategoryId()));
        SubcategoryEntity subcategoryEntity = subcategoryMapper.createRequestToEntity(request, categoryEntity);
        SubcategoryEntity savedEntity = subcategoryRepository.save(subcategoryEntity);
        return new SuccessDataResult<>(subcategoryMapper.toDetailsResponse(savedEntity), "Subcategory created successfully");
    }

    @Override
    public DataResult<SubcategoryDetailsResponse> getById(UUID id) {
        SubcategoryEntity entity = subcategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with id " + id));
        return new SuccessDataResult<>(subcategoryMapper.toDetailsResponse(entity), "Subcategory found successfully");
    }

    @Override
    @Transactional
    public DataResult<PageData<SubcategoryResponse>> getAll(int page, int size, UUID categoryId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt"));
        String message;
        Page<SubcategoryEntity> subcategoryPage;

        if (categoryId == null) {
            subcategoryPage = subcategoryRepository.findAll(pageable);
            message = "Subcategories found successfully";
        } else {
            CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + categoryId));
            subcategoryPage = subcategoryRepository.findAllByCategory(categoryEntity, pageable);
            message = "Subcategories found successfully for " + categoryEntity.getName();
        }

        PageData<SubcategoryResponse> subcategoryResponsePageData = new PageData<>(
                subcategoryPage.getTotalPages(),
                subcategoryPage.getTotalElements(),
                subcategoryPage.isFirst(),
                subcategoryPage.isLast(),
                subcategoryPage.getSize(),
                subcategoryPage.getNumber(),
                subcategoryMapper.toResponses(subcategoryPage.getContent())
        );
        return new SuccessDataResult<>(subcategoryResponsePageData, message);
    }

    @Override
    @Transactional
    public DataResult<SubcategoryDetailsResponse> update(UUID id, SubcategoryUpdateRequest request) {
        SubcategoryEntity subcategoryEntity = subcategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with id " + id));

        if (request.getCategoryId() != null) {
            CategoryEntity categoryEntity = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + request.getCategoryId()));
            subcategoryEntity.setCategory(categoryEntity);
        }

        subcategoryMapper.updateRequestToEntity(request, subcategoryEntity);
        return new SuccessDataResult<>(subcategoryMapper.toDetailsResponse(subcategoryEntity), "Subcategory updated successfully");
    }

    @Override
    public Result delete(UUID id) {
        SubcategoryEntity entity = subcategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with id " + id));
        subcategoryRepository.delete(entity);
        return new SuccessResult("Subcategory deleted successfully");
    }
}
