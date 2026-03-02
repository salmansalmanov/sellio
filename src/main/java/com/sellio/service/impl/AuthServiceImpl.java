package com.sellio.service.impl;

import com.sellio.exception.custom.InvalidInputException;
import com.sellio.factory.concrete.UserServiceFactory;
import com.sellio.model.dto.request.RegisterRequest;
import com.sellio.model.dto.response.core.UserResponse;
import com.sellio.model.enums.Role;
import com.sellio.model.result.DataResult;
import com.sellio.service.abstraction.AuthService;
import com.sellio.service.abstraction.UserService;
import com.sellio.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserUtil userUtil;
    private final UserServiceFactory userServiceFactory;

    @Override
    public DataResult<UserResponse> register(RegisterRequest registerRequest, String role, MultipartFile logo, MultipartFile banner) {
        userUtil.checkUser(registerRequest);
        Role roleEnum;
        try {
            roleEnum = Role.valueOf(role.toUpperCase());
        } catch (Exception e) {
            throw new InvalidInputException("Invalid role");
        }
        UserService userService = userServiceFactory.getServiceByRole(roleEnum);
        return userService.save(registerRequest, logo, banner);
    }
}
