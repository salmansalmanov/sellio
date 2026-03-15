package com.sellio.service.abstraction;

import com.sellio.model.dto.request.CategoryCreateRequest;
import com.sellio.model.dto.request.CategoryUpdateRequest;
import com.sellio.model.dto.response.core.CategoryDetailsResponse;
import com.sellio.model.dto.response.core.CategoryResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;

import java.util.UUID;

public interface CategoryService {
    DataResult<CategoryDetailsResponse> save(CategoryCreateRequest request);

    DataResult<PageData<CategoryResponse>> getAll(int page, int size);

    DataResult<CategoryDetailsResponse> getById(UUID id);

    DataResult<CategoryDetailsResponse> updateById(UUID id, CategoryUpdateRequest request);

    Result deleteById(UUID id);
}
