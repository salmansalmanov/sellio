package com.sellio.service.abstraction;

import com.sellio.model.dto.request.CityCreateRequest;
import com.sellio.model.dto.request.CityUpdateRequest;
import com.sellio.model.dto.response.core.CityResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;

import java.util.UUID;

public interface CityService {
    DataResult<CityResponse> save(CityCreateRequest request);

    DataResult<CityResponse> getById(UUID id);

    DataResult<PageData<CityResponse>> getAll(int page, int size);

    DataResult<CityResponse> update(UUID id, CityUpdateRequest request);

    Result delete(UUID id);
}
