package com.sellio.factory.abstraction;

import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.model.enums.Role;
import com.sellio.strategy.abstraction.UserStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseUserFactory {
    private final Map<Role, UserStrategy> strategyMap;

    public BaseUserFactory(List<UserStrategy> strategyList) {
        strategyMap = new HashMap<>();
        for (UserStrategy strategy : strategyList) {
            strategyMap.put(strategy.supports(), strategy);
        }
    }

    public UserStrategy getStrategy(String role) {
        Role roleEnum;

        try {
            roleEnum = Role.valueOf(role.toUpperCase());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Role " + role.toUpperCase() + " not found");
        }

        UserStrategy strategy = strategyMap.get(roleEnum);
        if (strategy == null) {
            throw new ResourceNotFoundException("Service not found for role: " + role.toUpperCase());
        }
        return strategy;
    }
}
