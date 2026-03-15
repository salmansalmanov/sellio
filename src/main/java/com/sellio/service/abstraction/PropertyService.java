package com.sellio.service.abstraction;

import com.sellio.model.dto.request.PropertyCreateRequest;
import com.sellio.model.dto.request.PropertyUpdateRequest;
import com.sellio.model.dto.response.core.PropertyDetailsResponse;
import com.sellio.model.dto.response.core.PropertyResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;

import java.util.UUID;

public interface PropertyService {
    DataResult<PropertyDetailsResponse> save(PropertyCreateRequest request);

    DataResult<PropertyDetailsResponse> getById(UUID id);

    DataResult<PageData<PropertyResponse>> getAll(UUID subcategoryId, int page, int size);

    DataResult<PropertyDetailsResponse> update(UUID id, PropertyUpdateRequest request);

    Result delete(UUID id);
}
