package com.sellio.service.impl

import com.sellio.factory.concrete.UserServiceFactory
import com.sellio.mapper.RefreshTokenMapper
import com.sellio.model.dto.request.LoginRequest
import com.sellio.model.dto.request.RegisterRequest
import com.sellio.model.dto.request.TokenRefreshRequest
import com.sellio.model.entity.AdminEntity
import com.sellio.model.entity.CustomerEntity
import com.sellio.model.entity.RefreshTokenEntity
import com.sellio.model.enums.Role
import com.sellio.repository.RefreshTokenRepository
import com.sellio.repository.UserRepository
import com.sellio.service.abstraction.UserService
import com.sellio.util.JwtUtil
import com.sellio.util.UserUtil
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import spock.lang.Specification
import spock.lang.Subject

class AuthServiceImplTest extends Specification {
    def userUtil = Mock(UserUtil)
    def userServiceFactory = Mock(UserServiceFactory)
    def authenticationManager = Mock(AuthenticationManager)
    def jwtUtil = Mock(JwtUtil)
    def userRepository = Mock(UserRepository)
    def refreshTokenRepository = Mock(RefreshTokenRepository)
    def refreshTokenMapper = Mock(RefreshTokenMapper)

    @Subject
    def authService = new AuthServiceImpl(
            userUtil, userServiceFactory, authenticationManager,
            jwtUtil, userRepository, refreshTokenRepository, refreshTokenMapper
    )

    def setup() {
        authService.refreshTokenExpiration = 604800000
    }

    def "register should delegate to correct service using factory"() {
        given:
        def request = Mock(RegisterRequest)
        def roleStr = "CUSTOMER"
        def userService = Mock(UserService)

        when:
        authService.register(request, roleStr, null, null)

        then:
        1 * userUtil.checkUser(request)
        1 * userServiceFactory.getServiceByRole(Role.CUSTOMER) >> userService
        1 * userService.save(request, null, null)
    }

    def "login should return access and refresh tokens for Customer"() {
        given:
        def loginRequest = new LoginRequest("salman@sellio.com", "password123")
        def customer = new CustomerEntity(email: "salman@sellio.com", role: Role.CUSTOMER)
        customer.id = UUID.randomUUID()

        def accessToken = "valid-access-token"
        def refreshTokenId = UUID.randomUUID()

        when:
        def result = authService.login(loginRequest)

        then:
        1 * authenticationManager.authenticate(_ as UsernamePasswordAuthenticationToken)
        1 * userRepository.findByIdentifier(loginRequest.usernameOrEmail) >> Optional.of(customer)
        1 * jwtUtil.generateAccessToken(customer.email, Role.CUSTOMER) >> accessToken

        and: "Handle refresh token logic"
        1 * refreshTokenRepository.findByUser(customer) >> Optional.empty()
        1 * refreshTokenMapper.toEntity(customer, _ as UUID) >> { user, uuid ->
            return new RefreshTokenEntity(user: user, refreshToken: uuid, isRevoked: false)
        }
        1 * refreshTokenRepository.save(_ as RefreshTokenEntity)

        expect:
        result.success
        result.data.accessToken == accessToken
        result.data.refreshToken != null
    }

    def "login should use username for Admin instead of email"() {
        given:
        def loginRequest = new LoginRequest("admin_user", "pass")
        def admin = new AdminEntity(username: "admin_user", role: Role.ADMIN)
        admin.id = UUID.randomUUID()

        when:
        authService.login(loginRequest)

        then:
        1 * authenticationManager.authenticate(_)
        1 * userRepository.findByIdentifier("admin_user") >> Optional.of(admin)
        1 * jwtUtil.generateAccessToken("admin_user", Role.ADMIN) >> "admin-token"
        1 * refreshTokenRepository.findByUser(admin) >> Optional.of(new RefreshTokenEntity())
        1 * refreshTokenRepository.save(_)
    }

    def "refreshToken should generate new tokens when valid"() {
        given:
        def oldToken = UUID.randomUUID()
        def user = new CustomerEntity(email: "salman@sellio.com", role: Role.CUSTOMER)
        def tokenEntity = new RefreshTokenEntity(refreshToken: oldToken, user: user, isRevoked: false)
        def request = new TokenRefreshRequest(oldToken)

        when:
        def result = authService.refreshToken(request)

        then:
        1 * refreshTokenRepository.findByRefreshToken(oldToken) >> Optional.of(tokenEntity)
        1 * jwtUtil.checkRefreshToken(tokenEntity)
        1 * jwtUtil.generateAccessToken(user.email, Role.CUSTOMER) >> "new-access-token"
        1 * refreshTokenRepository.save(tokenEntity)

        expect:
        result.success
        result.data.accessToken == "new-access-token"
        tokenEntity.refreshToken != oldToken
    }
}