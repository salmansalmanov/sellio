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
        CustomerRegisterRequest customerRegisterRequest = (CustomerRegisterRequest) registerRequest;
        CustomerEntity customerEntity = customerMapper.registerRequestToEntity(customerRegisterRequest);
        customerEntity.setStatus(UserStatus.ACTIVE);
        customerEntity.setPassword(passwordEncoder.encode(customerRegisterRequest.getPassword()));
        CustomerEntity savedEntity = userRepository.save(customerEntity);
        String key = "listing_count_" + savedEntity.getId();
        mailService.sendRegistrationMail(savedEntity.getEmail());
        redisTemplate.opsForValue().set(key, "0");
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
    @Transactional
    public DataResult<CustomerDetailsResponse> updateCustomerById(UUID id, CustomerUpdateRequest request) {
        String identifier = securityUtil.getCurrentUsernameOrEmail();
        UserEntity currentUserEntity = userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CustomerEntity targetEntity = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        if (currentUserEntity.getId().equals(targetEntity.getId()) ||
                currentUserEntity.getRole() == Role.SUPER_ADMIN ||
                currentUserEntity.getRole() == Role.ADMIN) {
            targetEntity = customerMapper.updateRequestToEntity(request, targetEntity);
            customerRepository.save(targetEntity);
            mailService.sendUpdateMail(targetEntity.getEmail());
            return new SuccessDataResult<>(customerMapper.toDetailsResponse(targetEntity), "Customer updated successfully");
        }
        throw new AccessDeniedException("Access denied");
    }

    @Override
    @Transactional
    public Result deleteCustomerById(UUID id) {
        String identifier = securityUtil.getCurrentUsernameOrEmail();
        UserEntity currentUserEntity = userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CustomerEntity targetEntity = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        if (currentUserEntity.getId().equals(targetEntity.getId()) ||
                currentUserEntity.getRole() == Role.SUPER_ADMIN ||
                currentUserEntity.getRole() == Role.ADMIN) {
            refreshTokenRepository.deleteByUser(targetEntity);
            for (ListingEntity listing : targetEntity.getListings()) {
                redisTemplate.delete("listing_view_count_" + listing.getId());
                redisTemplate.delete("listing_count_" + listing.getId());
            }
            customerRepository.delete(targetEntity);
            mailService.sendDeleteMail(targetEntity.getEmail());
            return new SuccessResult("Customer deleted successfully");
        }
        throw new AccessDeniedException("Access denied");
    }
}
