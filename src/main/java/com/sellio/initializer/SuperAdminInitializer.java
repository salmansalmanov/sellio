package com.sellio.initializer;

import com.sellio.model.entity.AdminEntity;
import com.sellio.model.enums.Role;
import com.sellio.model.enums.UserStatus;
import com.sellio.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuperAdminInitializer implements CommandLineRunner {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.super-admin.username}")
    private String username;

    @Value("${spring.super-admin.password}")
    private String password;

    @Override
    public void run(String @NonNull ... args) {
        log.info("Checking super admin exists or not");

        AdminEntity superAdmin = adminRepository.findByUsername(username)
                .orElse(new AdminEntity());

        superAdmin.setUsername(username);
        superAdmin.setPassword(passwordEncoder.encode(password));
        superAdmin.setFirstName("Super");
        superAdmin.setLastName("Admin");
        superAdmin.setStatus(UserStatus.ACTIVE);
        superAdmin.setRole(Role.SUPER_ADMIN);
        adminRepository.save(superAdmin);

        log.info("SuperAdmin has been saved successfully");
    }
}
