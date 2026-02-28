package com.sellio.service.abstraction;

import com.sellio.model.dto.request.AdminInviteRequest;

public interface AdminService extends UserService {
    void invite(AdminInviteRequest request);
}
