package com.sellio.service.impl;

import com.sellio.mapper.CustomerMapper;
import com.sellio.model.dto.request.CustomerRegisterRequest;
import com.sellio.model.dto.request.RegisterRequest;
import com.sellio.model.dto.response.core.UserResponse;
import com.sellio.model.entity.CustomerEntity;
import com.sellio.model.enums.UserStatus;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.SuccessDataResult;
import com.sellio.repository.UserRepository;
import com.sellio.service.abstraction.CustomerService;
import com.sellio.service.concrete.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerMapper customerMapper;
    private final UserRepository userRepository;
    private final MailService mailService;

    @Override
    public DataResult<UserResponse> save(RegisterRequest registerRequest, MultipartFile logo, MultipartFile banner) {
        CustomerRegisterRequest customerRegisterRequest = (CustomerRegisterRequest) registerRequest;
        CustomerEntity customerEntity = customerMapper.toEntity(customerRegisterRequest);
        customerEntity.setStatus(UserStatus.ACTIVE);
        CustomerEntity savedEntity = userRepository.save(customerEntity);
        mailService.sendRegistrationMail(savedEntity.getEmail());
        return new SuccessDataResult<>(customerMapper.toDetailsResponse(savedEntity), "Customer registered successfully");
    }
}
