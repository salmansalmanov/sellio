package com.sellio.mapper;

import com.sellio.model.dto.request.CustomerRegisterRequest;
import com.sellio.model.dto.request.CustomerUpdateRequest;
import com.sellio.model.dto.response.core.CustomerDetailsResponse;
import com.sellio.model.dto.response.core.CustomerResponse;
import com.sellio.model.entity.CustomerEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "role", constant = "CUSTOMER")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "phoneNumbers", source = "phoneNumber")
    CustomerEntity toEntity(CustomerRegisterRequest customerRegisterRequest);

    @Mapping(target = "phoneNumber", source = "phoneNumbers")
    CustomerDetailsResponse toDetailsResponse(CustomerEntity customerEntity);

    default List<String> map(String phoneNumber) {
        if (phoneNumber == null) {
            return new ArrayList<>();
        }
        return List.of(phoneNumber);
    }

    default String map(List<String> phoneNumbers) {
        if (phoneNumbers == null) {
            return null;
        }
        return phoneNumbers.getFirst();
    }

    CustomerResponse toResponse(CustomerEntity customerEntity);

    @AfterMapping
    default void afterMapping(CustomerEntity source, @MappingTarget CustomerResponse target) {
        target.setFullName(source.getFirstName() + " " + source.getLastName());
    }

    default List<CustomerResponse> toResponses(List<CustomerEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    CustomerEntity updateRequestToEntity(CustomerUpdateRequest request, @MappingTarget CustomerEntity customerEntity);

    @AfterMapping
    default void afterMapping(CustomerUpdateRequest source, @MappingTarget CustomerEntity target) {
        target.setPhoneNumbers(new ArrayList<>());
        target.getPhoneNumbers().add(source.getPhoneNumber());
    }
}
