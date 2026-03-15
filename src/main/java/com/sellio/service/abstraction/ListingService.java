package com.sellio.service.abstraction;

import com.sellio.model.dto.request.ListingCreateRequest;
import com.sellio.model.dto.request.ListingUpdateRequest;
import com.sellio.model.dto.response.core.ListingDetailsResponse;
import com.sellio.model.dto.response.core.ListingResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ListingService {
    DataResult<ListingDetailsResponse> save(ListingCreateRequest request, List<MultipartFile> images) throws IOException;

    DataResult<ListingDetailsResponse> getById(UUID id);

    DataResult<PageData<ListingResponse>> getAll(UUID ownerId, int page, int size);

    DataResult<ListingDetailsResponse> update(UUID id, ListingUpdateRequest request,  List<MultipartFile> images) throws IOException;

    DataResult<ListingDetailsResponse> deactivate(UUID id);

    DataResult<ListingDetailsResponse> activate(UUID id);
}
