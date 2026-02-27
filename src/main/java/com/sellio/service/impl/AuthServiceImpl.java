package com.sellio.service.impl;

import com.sellio.exception.custom.AlreadyExistsException;
import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.factory.concrete.UserServiceFactory;
import com.sellio.model.dto.request.AdminRegisterRequest;
import com.sellio.model.dto.request.CustomerRegisterRequest;
import com.sellio.model.dto.request.RegisterRequest;
import com.sellio.model.dto.request.ShopRegisterRequest;
import com.sellio.model.dto.response.core.UserResponse;
import com.sellio.model.enums.Role;
import com.sellio.model.result.DataResult;
import com.sellio.repository.AdminRepository;
import com.sellio.repository.ShopRepository;
import com.sellio.repository.UserRepository;
import com.sellio.service.abstraction.AuthService;
import com.sellio.service.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final UserServiceFactory userServiceFactory;
    private final ShopRepository shopRepository;

    @Override
    public DataResult<UserResponse> register(RegisterRequest registerRequest, String role, MultipartFile logo, MultipartFile banner) {
        checkAccount(registerRequest);
        Role roleEnum;
        try {
            roleEnum = Role.valueOf(role.toUpperCase());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Invalid role");
        }
        UserService userService = userServiceFactory.getServiceByRole(roleEnum);
        return userService.save(registerRequest, logo, banner);
    }

    private void checkAccount(RegisterRequest registerRequest) {
        if (registerRequest instanceof CustomerRegisterRequest customerRegisterRequest) {
            if (userRepository.existsByEmail(customerRegisterRequest.getEmail())) {
                throw new AlreadyExistsException("Email already exists");
            }
            if (userRepository.existsByPhoneNumber(customerRegisterRequest.getPhoneNumber())) {
                throw new AlreadyExistsException("Phone number already exists");
            }
        } else if (registerRequest instanceof ShopRegisterRequest shopRegisterRequest) {
            if (userRepository.existsByEmail(shopRegisterRequest.getEmail())) {
                throw new AlreadyExistsException("Email already exists");
            }
            if (userRepository.existsByPhoneNumbers(shopRegisterRequest.getPhoneNumbers())) {
                throw new AlreadyExistsException("Phone number already exists");
            }
            if (shopRepository.existsByName(shopRegisterRequest.getName())) {
                throw new AlreadyExistsException("Name already exists");
            }
        } else {
            AdminRegisterRequest adminRegisterRequest = (AdminRegisterRequest) registerRequest;
            if (adminRepository.existsByUsername(adminRegisterRequest.getUsername())) {
                throw new AlreadyExistsException("Username already exists");
            }
        }
    }
}
