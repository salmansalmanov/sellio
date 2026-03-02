package com.sellio.service.abstraction;

import com.sellio.model.dto.response.core.CustomerResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;

public interface CustomerService extends UserService {
    DataResult<PageData<CustomerResponse>> getAllCustomers(int page, int size);
}
