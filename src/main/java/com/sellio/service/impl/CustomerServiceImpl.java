package com.sellio.service.impl;

import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.mapper.CustomerMapper;
import com.sellio.model.dto.request.CustomerRegisterRequest;
import com.sellio.model.dto.request.CustomerUpdateRequest;
import com.sellio.model.dto.request.RegisterRequest;
import com.sellio.model.dto.response.core.CustomerDetailsResponse;
import com.sellio.model.dto.response.core.CustomerResponse;
import com.sellio.model.dto.response.core.UserResponse;
import com.sellio.model.entity.CustomerEntity;
import com.sellio.model.enums.UserStatus;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.SuccessDataResult;
import com.sellio.repository.CustomerRepository;
import com.sellio.repository.UserRepository;
import com.sellio.service.abstraction.CustomerService;
import com.sellio.service.concrete.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerMapper customerMapper;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final CustomerRepository customerRepository;

    @Override
    public DataResult<UserResponse> save(RegisterRequest registerRequest, MultipartFile logo, MultipartFile banner) {
        CustomerRegisterRequest customerRegisterRequest = (CustomerRegisterRequest) registerRequest;
        CustomerEntity customerEntity = customerMapper.registerRequestToEntity(customerRegisterRequest);
        customerEntity.setStatus(UserStatus.ACTIVE);
        CustomerEntity savedEntity = userRepository.save(customerEntity);
        mailService.sendRegistrationMail(savedEntity.getEmail());
        return new SuccessDataResult<>(customerMapper.toDetailsResponse(savedEntity), "Customer registered successfully");
    }

    @Override
    public DataResult<PageData<CustomerResponse>> getAllCustomers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CustomerEntity> customerPage = customerRepository.findAll(pageable);
        PageData<CustomerResponse> customerResponsePageData = new PageData<>(
                customerPage.getTotalPages(),
                customerPage.getTotalElements(),
                customerPage.isFirst(),
                customerPage.isLast(),
                customerPage.getSize(),
                customerPage.getNumber(),
                customerMapper.toResponses(customerPage.getContent())
        );
        return new SuccessDataResult<>(customerResponsePageData, "Customers found successfully");
    }

    @Override
    public DataResult<CustomerDetailsResponse> getCustomerById(UUID id) {
        CustomerEntity customerEntity = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return new SuccessDataResult<>(customerMapper.toDetailsResponse(customerEntity), "Customer found successfully");
    }

    @Override
    public DataResult<CustomerDetailsResponse> updateCustomerById(UUID id, CustomerUpdateRequest request) {
        CustomerEntity customerEntity = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customerEntity = customerMapper.updateRequestToEntity(request, customerEntity);
        customerRepository.save(customerEntity);
        mailService.sendUpdateEmail(customerEntity.getEmail());
        return new SuccessDataResult<>(customerMapper.toDetailsResponse(customerEntity), "Customer updated successfully");
    }

    @Override
    public void deleteCustomerById(UUID id) {
        CustomerEntity customerEntity = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customerRepository.delete(customerEntity);
        mailService.sendDeleteEmail(customerEntity.getEmail());
    }
}
