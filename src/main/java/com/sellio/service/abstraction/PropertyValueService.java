package com.sellio.service.abstraction;

import com.sellio.model.dto.request.PropertyValueAddRequest;
import com.sellio.model.dto.request.PropertyValueUpdateRequest;
import com.sellio.model.dto.response.core.PropertyValueDetailsResponse;
import com.sellio.model.dto.response.core.PropertyValueResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

public interface PropertyValueService {
    DataResult<PropertyValueDetailsResponse> save(@Valid @RequestBody PropertyValueAddRequest request);

    DataResult<PropertyValueDetailsResponse> getById(UUID id);

    DataResult<PageData<PropertyValueResponse>> getAll(UUID propertyId, int page, int size);

    DataResult<PropertyValueDetailsResponse> update(UUID id, PropertyValueUpdateRequest request);

    Result delete(UUID id);
}
