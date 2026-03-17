package com.sellio.service.concrete

import com.sellio.exception.custom.ResourceNotFoundException
import com.sellio.model.entity.AdminEntity
import com.sellio.model.entity.CustomerEntity
import com.sellio.model.entity.ShopEntity
import com.sellio.model.enums.Role
import com.sellio.repository.UserRepository
import spock.lang.Specification
import spock.lang.Subject

class CustomUserDetailsServiceTest extends Specification {
    def userRepository = Mock(UserRepository)

    @Subject
    def customUserDetailsService = new CustomUserDetailsService(userRepository)

    def "should load UserDetails for Customer using email as principal"() {
        given: "A customer entity"
        def identifier = "customer@sellio.com"
        def customer = CustomerEntity.builder()
                .id(UUID.randomUUID())
                .email(identifier)
                .password("password123")
                .role(Role.CUSTOMER)
                .build()

        when:
        def userDetails = customUserDetailsService.loadUserByUsername(identifier)

        then:
        1 * userRepository.findByIdentifier(identifier) >> Optional.of(customer)

        expect:
        userDetails.username == identifier
        userDetails.password == "password123"
        userDetails.authorities.any { it.authority == "ROLE_CUSTOMER" }
    }

    def "should load UserDetails for Admin using username as principal"() {
        given: "An admin entity with no email but a username"
        def identifier = "admin_salman"
        def admin = AdminEntity.builder()
                .id(UUID.randomUUID())
                .email(null)
                .username(identifier)
                .password("adminPass")
                .role(Role.ADMIN)
                .build()

        when:
        def userDetails = customUserDetailsService.loadUserByUsername(identifier)

        then:
        1 * userRepository.findByIdentifier(identifier) >> Optional.of(admin)

        expect:
        userDetails.username == identifier
        userDetails.authorities.any { it.authority == "ROLE_ADMIN" }
    }

    def "should load UserDetails for Shop using email as principal"() {
        given: "A shop entity"
        def identifier = "shop@sellio.com"
        def shop = ShopEntity.builder()
                .id(UUID.randomUUID())
                .email(identifier)
                .name("My Shop")
                .password("shopPass")
                .role(Role.SHOP)
                .build()

        when:
        def userDetails = customUserDetailsService.loadUserByUsername(identifier)

        then:
        1 * userRepository.findByIdentifier(identifier) >> Optional.of(shop)

        expect:
        userDetails.username == identifier
        userDetails.authorities.any { it.authority == "ROLE_SHOP" }
    }

    def "should throw ResourceNotFoundException when user identifier is not found"() {
        given:
        def identifier = "unknown_user"

        when:
        customUserDetailsService.loadUserByUsername(identifier)

        then:
        1 * userRepository.findByIdentifier(identifier) >> Optional.empty()
        thrown(ResourceNotFoundException)
    }
}