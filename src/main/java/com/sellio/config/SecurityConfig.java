package com.sellio.config;

import com.sellio.exception.handler.CustomAccessDeniedHandler;
import com.sellio.exception.handler.JwtAuthenticationEntryPoint;
import com.sellio.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(
                                        "/v2/api-docs",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-resources/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/webjars/**",
                                        "/actuator/**"
                                ).permitAll()
                                .requestMatchers("/v1/auth/**").permitAll()
                                .requestMatchers("/v1/admins/invite").hasRole("SUPER_ADMIN")
                                .requestMatchers("/v1/admins/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/v1/categories").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/v1/categories/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "CUSTOMER", "SHOP")
                                .requestMatchers(HttpMethod.PUT, "/v1/categories/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/v1/categories/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/v1/cities/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/v1/cities/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "CUSTOMER", "SHOP")
                                .requestMatchers(HttpMethod.PUT, "/v1/cities/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/v1/cities/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/v1/customers/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/v1/customers/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "CUSTOMER")
                                .requestMatchers(HttpMethod.DELETE, "/v1/customers/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "CUSTOMER")
                                .requestMatchers(HttpMethod.POST, "/v1/listings").hasAnyRole("CUSTOMER", "SHOP")
                                .requestMatchers(HttpMethod.GET, "/v1/listings/**").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/v1/listings/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "CUSTOMER", "SHOP")
                                .requestMatchers(HttpMethod.PATCH, "/v1/listings/deactivate/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "CUSTOMER", "SHOP")
                                .requestMatchers(HttpMethod.PATCH, "/v1/listings/activate/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "CUSTOMER", "SHOP")
                                .requestMatchers(HttpMethod.DELETE, "/v1/listings/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/v1/properties").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/v1/properties/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/v1/properties/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/v1/properties/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "CUSTOMER", "SHOP")
                                .requestMatchers("/v1/property-dependencies/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/v1/property-values").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/v1/property-values/**").hasAnyRole("CUSTOMER", "SHOP")
                                .requestMatchers(HttpMethod.PUT, "/v1/property-values/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/v1/property-values/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/v1/shops/**").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/v1/shops/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "SHOP")
                                .requestMatchers(HttpMethod.DELETE, "/v1/shops/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "SHOP")
                                .requestMatchers(HttpMethod.POST, "/v1/subcategories").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/v1/subcategories/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/v1/subcategories/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/v1/subcategories/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "CUSTOMER", "SHOP")
                                .requestMatchers("/v1/pricing/change-plan/**").hasAnyRole("CUSTOMER", "SHOP")
                                .anyRequest().authenticated())
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }
}
