package com.sellio.util;

import com.sellio.model.entity.ListingEntity;
import com.sellio.model.entity.UserEntity;
import com.sellio.model.enums.Role;
import com.sellio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.springframework.security.access.AccessDeniedException;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final UserRepository userRepository;

    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Access Denied");
        }

        return userRepository.findByIdentifier(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("User not found"));
    }

    public void validateAccess(UserEntity targetEntity) {
        UserEntity currentUser = getCurrentUser();
        boolean isOwner = currentUser.getId().equals(targetEntity.getId());
        boolean isSuperAdmin = currentUser.getRole() == Role.SUPER_ADMIN;
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (isSuperAdmin) {
            return;
        }

        if (isOwner) {
            return;
        }

        if (isAdmin && (targetEntity.getRole() == Role.SHOP || targetEntity.getRole() == Role.CUSTOMER)) {
            return;
        }

        throw new AccessDeniedException("Access denied");
    }

    public void validateListingAccess(ListingEntity listingEntity) {
        validateAccess(listingEntity.getOwner());
    }
}
