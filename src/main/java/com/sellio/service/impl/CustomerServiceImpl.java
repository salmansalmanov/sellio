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
import com.sellio.model.entity.ListingEntity;
import com.sellio.model.entity.UserEntity;
import com.sellio.model.enums.Role;
import com.sellio.model.enums.UserStatus;
import com.sellio.model.result.*;
import com.sellio.repository.CustomerRepository;
import com.sellio.repository.RefreshTokenRepository;
import com.sellio.repository.UserRepository;
import com.sellio.service.abstraction.CustomerService;
import com.sellio.service.concrete.MailService;
import com.sellio.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerMapper customerMapper;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public DataResult<UserResponse> save(RegisterRequest registerRequest, MultipartFile logo, MultipartFile banner) {
        log.info("CustomerServiceImpl.save.start: {}", registerRequest);
        CustomerRegisterRequest customerRegisterRequest = (CustomerRegisterRequest) registerRequest;
        CustomerEntity customerEntity = customerMapper.registerRequestToEntity(customerRegisterRequest);
        customerEntity.setStatus(UserStatus.ACTIVE);
        customerEntity.setPassword(passwordEncoder.encode(customerRegisterRequest.getPassword()));
        CustomerEntity savedEntity = userRepository.save(customerEntity);
        String key = "listing_count_" + savedEntity.getId();
        mailService.sendRegistrationMail(savedEntity.getEmail());
        redisTemplate.opsForValue().set(key, "0");
        log.info("CustomerServiceImpl.save.end: {}", savedEntity);
        return new SuccessDataResult<>(customerMapper.toDetailsResponse(savedEntity), "Customer registered successfully");
    }

    @Override
    public DataResult<PageData<CustomerResponse>> getAllCustomers(int page, int size) {
        log.info("CustomerServiceImpl.getAllCustomers.start: {}", page);
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
        log.info("CustomerServiceImpl.getAllCustomers.end: {}", customerResponsePageData);
        return new SuccessDataResult<>(customerResponsePageData, "Customers found successfully");
    }

    @Override
    public DataResult<CustomerDetailsResponse> getCustomerById(UUID id) {
        log.info("CustomerServiceImpl.getCustomerById.start: {}", id);
        CustomerEntity customerEntity = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        log.info("CustomerServiceImpl.getCustomerById.end: {}", customerEntity);
        return new SuccessDataResult<>(customerMapper.toDetailsResponse(customerEntity), "Customer found successfully");
    }

    @Override
    @Transactional
    public DataResult<CustomerDetailsResponse> updateCustomerById(UUID id, CustomerUpdateRequest request) {
        log.info("CustomerServiceImpl.updateCustomerById.start: {}", id);
        CustomerEntity targetEntity = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        securityUtil.validateAccess(targetEntity);
        targetEntity = customerMapper.updateRequestToEntity(request, targetEntity);
        customerRepository.save(targetEntity);
        mailService.sendUpdateMail(targetEntity.getEmail());
        log.info("CustomerServiceImpl.updateCustomerById.end: {}", targetEntity);
        return new SuccessDataResult<>(customerMapper.toDetailsResponse(targetEntity), "Customer updated successfully");
    }

    @Override
    @Transactional
    public Result deleteCustomerById(UUID id) {
        log.info("CustomerServiceImpl.deleteCustomerById.start: {}", id);
        CustomerEntity targetEntity = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        securityUtil.validateAccess(targetEntity);
        refreshTokenRepository.deleteByUser(targetEntity);
        for (ListingEntity listing : targetEntity.getListings()) {
            redisTemplate.delete("listing_view_count_" + listing.getId());
            redisTemplate.delete("listing_count_" + listing.getId());
        }
        customerRepository.delete(targetEntity);
        mailService.sendDeleteMail(targetEntity.getEmail());
        log.info("CustomerServiceImpl.deleteCustomerById.end: {}", targetEntity);
        return new SuccessResult("Customer deleted successfully");
    }
}
