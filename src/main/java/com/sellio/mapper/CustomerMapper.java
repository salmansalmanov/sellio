package com.sellio.mapper;

import com.sellio.model.dto.request.CustomerRegisterRequest;
import com.sellio.model.dto.request.CustomerUpdateRequest;
import com.sellio.model.dto.response.core.CustomerDetailsResponse;
import com.sellio.model.dto.response.core.CustomerResponse;
import com.sellio.model.entity.CustomerEntity;
import com.sellio.model.enums.Role;
import com.sellio.model.enums.UserStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class CustomerMapper {
    public CustomerEntity registerRequestToEntity(CustomerRegisterRequest request) {
        return CustomerEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumbers(Set.of(request.getPhoneNumber()))
                .pricingPlan(request.getPricingPlan())
                .status(UserStatus.PENDING)
                .role(Role.CUSTOMER)
                .build();
    }

    public CustomerDetailsResponse toDetailsResponse(CustomerEntity entity) {
        Set<String> phones = entity.getPhoneNumbers();
        String phoneNumber = null;
        for (String phone : phones) {
            if (phone != null) {
                phoneNumber = phone;
            }
        }
        return CustomerDetailsResponse.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .phoneNumber(phoneNumber)
                .email(entity.getEmail())
                .status(entity.getStatus())
                .role(entity.getRole())
                .build();
    }

    public CustomerResponse toResponse(CustomerEntity entity) {
        return CustomerResponse.builder()
                .id(entity.getId())
                .fullName(entity.getFirstName() + " " + entity.getLastName())
                .status(entity.getStatus())
                .build();
    }

    public List<CustomerResponse> toResponses(List<CustomerEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    public CustomerEntity updateRequestToEntity(CustomerUpdateRequest request, CustomerEntity entity) {
        if (request.getFirstName() != null) {
            entity.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            entity.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            Set<String> phoneNumbers = new HashSet<>();
            phoneNumbers.add(request.getPhoneNumber());
            entity.setPhoneNumbers(phoneNumbers);
        }
        return entity;
    }
}
