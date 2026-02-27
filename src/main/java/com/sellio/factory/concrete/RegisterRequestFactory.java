package com.sellio.factory.concrete;

import com.sellio.factory.abstraction.BaseUserFactory;
import com.sellio.model.dto.request.RegisterRequest;
import com.sellio.model.enums.Role;
import com.sellio.strategy.abstraction.UserStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RegisterRequestFactory extends BaseUserFactory {
    public RegisterRequestFactory(List<UserStrategy> strategyList) {
        super(strategyList);
    }

    public Class<? extends RegisterRequest> getRequestClassByRole(Role role) {
        return getStrategy(role).getRequestClass();
    }
}
