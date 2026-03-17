package com.sellio.service.concrete;

import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.model.entity.AdminEntity;
import com.sellio.model.entity.UserEntity;
import com.sellio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("ActionLog.loadUserByUsername.start: {}", username);
        UserEntity userEntity = userRepository.findByIdentifier(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with identifier: " + username));

        String principal = userEntity.getEmail() != null ? userEntity.getEmail() : ((AdminEntity) userEntity).getUsername();
        log.info("ActionLog.loadUserByUsername.end: {}", username);
        return User.builder()
                .username(principal)
                .password(userEntity.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().name()))
                .build();
    }
}
