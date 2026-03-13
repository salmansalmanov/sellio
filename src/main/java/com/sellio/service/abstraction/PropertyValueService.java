package com.sellio.service.abstraction;

import com.sellio.model.dto.request.PropertyValueAddRequest;
import com.sellio.model.dto.request.PropertyValueUpdateRequest;
import com.sellio.model.dto.response.core.PropertyValueGetResponse;
import com.sellio.model.dto.response.core.PropertyValueResponse;
import com.sellio.model.dto.response.core.PropertyValueSaveResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;

import java.util.UUID;

public interface PropertyValueService {
    DataResult<PropertyValueSaveResponse> save(PropertyValueAddRequest request);

    DataResult<PropertyValueGetResponse> getById(UUID id);

    DataResult<PageData<PropertyValueResponse>> getAll(UUID propertyId, int page, int size);

    DataResult<PropertyValueGetResponse> update(UUID id, PropertyValueUpdateRequest request);

    Result delete(UUID id);
}
