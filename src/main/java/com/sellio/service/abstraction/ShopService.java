package com.sellio.service.abstraction;

import com.sellio.model.dto.response.core.ShopResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;

public interface ShopService extends UserService {
    DataResult<PageData<ShopResponse>> getAllShops(int page, int size);
}
