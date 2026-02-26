package com.sellio.factory.concrete;

import com.sellio.factory.abstraction.BaseUserFactory;
import com.sellio.service.abstraction.UserService;
import com.sellio.strategy.abstraction.UserStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserServiceFactory extends BaseUserFactory {
    public UserServiceFactory(List<UserStrategy> strategyList) {
        super(strategyList);
    }

    public UserService getServiceByRole(String role) {
        return getStrategy(role).getService();
    }
}
