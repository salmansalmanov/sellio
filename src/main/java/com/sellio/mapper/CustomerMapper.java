package com.sellio.mapper;

import com.sellio.model.dto.request.CustomerRegisterRequest;
import com.sellio.model.dto.response.core.CustomerDetailsResponse;
import com.sellio.model.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

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
}
