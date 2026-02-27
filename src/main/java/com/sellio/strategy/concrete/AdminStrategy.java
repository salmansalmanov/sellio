package com.sellio.strategy.concrete;

import com.sellio.model.dto.request.AdminRegisterRequest;
import com.sellio.model.dto.request.RegisterRequest;
import com.sellio.model.enums.Role;
import com.sellio.service.abstraction.AdminService;
import com.sellio.service.abstraction.UserService;
import com.sellio.strategy.abstraction.UserStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminStrategy implements UserStrategy {
    private final AdminService adminService;

    @Override
    public Role supports() {
        return Role.ADMIN;
    }

    @Override
    public UserService getService() {
        return adminService;
    }

    @Override
    public Class<? extends RegisterRequest> getRequestClass() {
        return AdminRegisterRequest.class;
    }
}
