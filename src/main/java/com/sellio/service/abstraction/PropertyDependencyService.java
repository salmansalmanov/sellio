package com.sellio.service.abstraction;

import com.sellio.model.dto.request.PropertyDependencyRequest;
import com.sellio.model.dto.response.core.PropertyDependencyDetailsResponse;
import com.sellio.model.dto.response.core.PropertyDependencyResponse;
import com.sellio.model.dto.response.core.PropertyValueResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;

import java.util.UUID;

public interface PropertyDependencyService {
    DataResult<PropertyDependencyDetailsResponse> save(PropertyDependencyRequest request);

    DataResult<PropertyDependencyResponse> getById(UUID id);

    DataResult<PageData<PropertyDependencyResponse>> getAll(UUID parentPropertyValueId, int page, int size);

    Result deleteById(UUID id);
}
