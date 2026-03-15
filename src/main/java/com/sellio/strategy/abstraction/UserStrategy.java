package com.sellio.strategy.abstraction;

import com.sellio.model.dto.request.RegisterRequest;
import com.sellio.model.enums.Role;
import com.sellio.service.abstraction.UserService;

public interface UserStrategy {
    Role supports();

    UserService getService();

    Class<? extends RegisterRequest> getRequestClass();
}
