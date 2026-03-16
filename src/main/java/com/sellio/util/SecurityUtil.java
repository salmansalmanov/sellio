package com.sellio.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.springframework.security.access.AccessDeniedException;

@Component
public class SecurityUtil {
    public String getCurrentUsernameOrEmail() throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Access Denied");
        }
        return authentication.getName();
    }
}
