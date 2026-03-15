package com.sellio.strategy.concrete;

import com.sellio.model.dto.request.CustomerRegisterRequest;
import com.sellio.model.dto.request.RegisterRequest;
import com.sellio.model.enums.Role;
import com.sellio.service.abstraction.CustomerService;
import com.sellio.service.abstraction.UserService;
import com.sellio.strategy.abstraction.UserStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerStrategy implements UserStrategy {
    private final CustomerService customerService;

    @Override
    public Role supports() {
        return Role.CUSTOMER;
    }

    @Override
    public UserService getService() {
        return customerService;
    }

    @Override
    public Class<? extends RegisterRequest> getRequestClass() {
        return CustomerRegisterRequest.class;
    }
}
