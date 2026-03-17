package com.sellio.service.abstraction;

import com.sellio.model.dto.request.AdminInviteRequest;
import com.sellio.model.dto.request.AdminUpdateRequest;
import com.sellio.model.dto.response.core.AdminDetailsResponse;
import com.sellio.model.dto.response.core.AdminResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;

import java.util.UUID;

public interface AdminService extends UserService {
    Result invite(AdminInviteRequest request);

    DataResult<PageData<AdminResponse>> getAllAdmins(int page, int size);

    DataResult<AdminDetailsResponse> getAdminById(UUID id);

    DataResult<AdminDetailsResponse> updateAdminById(UUID id, AdminUpdateRequest request);

    Result deleteAdminById(UUID id);
}
