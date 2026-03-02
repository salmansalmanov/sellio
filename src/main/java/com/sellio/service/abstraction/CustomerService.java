package com.sellio.service.abstraction;

import com.sellio.model.dto.request.CustomerUpdateRequest;
import com.sellio.model.dto.response.core.CustomerDetailsResponse;
import com.sellio.model.dto.response.core.CustomerResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;

import java.util.UUID;

public interface CustomerService extends UserService {
    DataResult<PageData<CustomerResponse>> getAllCustomers(int page, int size);

    DataResult<CustomerDetailsResponse> getCustomerById(UUID id);

    DataResult<CustomerDetailsResponse> updateCustomerById(UUID id, CustomerUpdateRequest request);
}
