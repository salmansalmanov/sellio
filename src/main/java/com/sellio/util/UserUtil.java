package com.sellio.util;

import com.sellio.exception.custom.AlreadyExistsException;
import com.sellio.model.dto.request.*;
import com.sellio.repository.AdminRepository;
import com.sellio.repository.ShopRepository;
import com.sellio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUtil {
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final AdminRepository adminRepository;

    public void checkUser(RegisterRequest registerRequest) {
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

    public void checkShop(ShopUpdateRequest shopUpdateRequest) {
        if (shopRepository.existsByName(shopUpdateRequest.getName())) {
            throw new AlreadyExistsException("Shop name already exists");
        }
        if (userRepository.existsByPhoneNumbers(shopUpdateRequest.getPhoneNumbers())) {
            throw new AlreadyExistsException("Phone number already exists");
        }
    }
}
