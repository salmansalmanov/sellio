package com.sellio.service.abstraction;

import com.sellio.model.dto.request.ShopUpdateRequest;
import com.sellio.model.dto.response.core.ShopDetailsResponse;
import com.sellio.model.dto.response.core.ShopResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface ShopService extends UserService {
    DataResult<PageData<ShopResponse>> getAllShops(int page, int size);

    DataResult<ShopDetailsResponse> getShopById(UUID id);

    DataResult<ShopDetailsResponse> updateShopById(UUID id, ShopUpdateRequest request, MultipartFile logo, MultipartFile banner) throws IOException;

    Result deleteShopById(UUID id);
}
