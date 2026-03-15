package com.sellio.service.abstraction;

import com.sellio.model.dto.request.SubcategoryCreateRequest;
import com.sellio.model.dto.request.SubcategoryUpdateRequest;
import com.sellio.model.dto.response.core.SubcategoryDetailsResponse;
import com.sellio.model.dto.response.core.SubcategoryResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;

import java.util.UUID;

public interface SubcategoryService {
    DataResult<SubcategoryDetailsResponse> save(SubcategoryCreateRequest request);

    DataResult<SubcategoryDetailsResponse> getById(UUID id);

    DataResult<PageData<SubcategoryResponse>> getAll(int page, int size, UUID categoryId);

    DataResult<SubcategoryDetailsResponse> update(UUID id, SubcategoryUpdateRequest request);

    Result delete(UUID id);
}
