package com.sellio.service.abstraction;

import com.sellio.model.dto.request.AdminInviteRequest;
import com.sellio.model.dto.response.core.AdminResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;

public interface AdminService extends UserService {
    void invite(AdminInviteRequest request);

    DataResult<PageData<AdminResponse>> getAllAdmins(int page, int size);
}
