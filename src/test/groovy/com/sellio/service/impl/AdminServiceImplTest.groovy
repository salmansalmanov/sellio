package com.sellio.service.impl

import com.sellio.exception.custom.AlreadyExistsException
import com.sellio.exception.custom.InvalidInputException
import com.sellio.exception.custom.ResourceNotFoundException
import com.sellio.mapper.AdminMapper
import com.sellio.model.dto.response.core.AdminDetailsResponse;
import com.sellio.model.dto.request.AdminInviteRequest
import com.sellio.model.dto.request.AdminRegisterRequest
import com.sellio.model.dto.request.AdminUpdateRequest
import com.sellio.model.entity.AdminEntity
import com.sellio.model.enums.Role
import com.sellio.repository.AdminRepository
import com.sellio.repository.RefreshTokenRepository
import com.sellio.repository.UserRepository
import com.sellio.service.concrete.MailService
import com.sellio.util.SecurityUtil
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification
import spock.lang.Subject

import java.util.concurrent.TimeUnit

class AdminServiceImplTest extends Specification {
    def userRepository = Mock(UserRepository)
    def redisTemplate = Mock(RedisTemplate)
    def valueOperations = Mock(ValueOperations)
    def mailService = Mock(MailService)
    def adminMapper = Mock(AdminMapper)
    def adminRepository = Mock(AdminRepository)
    def passwordEncoder = Mock(PasswordEncoder)
    def refreshTokenRepository = Mock(RefreshTokenRepository)
    def securityUtil = Mock(SecurityUtil)

    @Subject
    def adminService = new AdminServiceImpl(
            userRepository, redisTemplate, mailService, adminMapper,
            adminRepository, passwordEncoder, refreshTokenRepository, securityUtil
    )

    def setup() {
        redisTemplate.opsForValue() >> valueOperations
    }

    def "invite should throw exception if email exists"() {
        given:
        def request = new AdminInviteRequest(email: "test@sellio.com")

        when:
        adminService.invite(request)

        then:
        1 * userRepository.existsByEmail(request.email) >> true
        thrown(AlreadyExistsException)
    }

    def "invite should generate token and send mail if first time"() {
        given:
        def request = new AdminInviteRequest(email: "new@sellio.com")
        def key = "invite_new@sellio.com"

        when:
        adminService.invite(request)

        then:
        1 * userRepository.existsByEmail(request.email) >> false
        1 * redisTemplate.hasKey(key) >> false
        1 * valueOperations.set(key, _ as String, 24, TimeUnit.HOURS)
        1 * mailService.sendAdminInvitationMail(request.email, _ as String)
    }

    def "save should throw exception if token is invalid"() {
        given:
        def request = new AdminRegisterRequest(email: "test@sellio.com", token: "wrong_token")
        def key = "invite_test@sellio.com"

        when:
        adminService.save(request, null, null)

        then:
        1 * valueOperations.get(key) >> "correct_token"
        thrown(InvalidInputException)
    }

    def "save should register admin successfully if token matches"() {
        given:
        def email = "admin@sellio.com"
        def token = "valid_token"
        def request = new AdminRegisterRequest(email: email, token: token, password: "123")
        def entity = new AdminEntity(email: email)
        def savedEntity = new AdminEntity(email: email, id: UUID.randomUUID())

        when:
        def result = adminService.save(request, null, null)

        then:
        1 * valueOperations.get("invite_" + email) >> token
        1 * adminMapper.registerRequestToEntity(request) >> entity
        1 * passwordEncoder.encode("123") >> "encoded_123"
        1 * userRepository.save(entity) >> savedEntity
        1 * redisTemplate.delete("invite_" + email)
        1 * mailService.sendRegistrationMail(email)
        1 * adminMapper.toDetailsResponse(savedEntity) >> Mock(AdminDetailsResponse)

        expect:
        result.success
    }

    def "updateAdminById should delete refresh token if username changes"() {
        given:
        def id = UUID.randomUUID()
        def request = new AdminUpdateRequest(username: "new_user")
        def targetEntity = new AdminEntity(id: id, username: "old_user", email: "a@a.com")

        when:
        adminService.updateAdminById(id, request)

        then:
        1 * adminRepository.findById(id) >> Optional.of(targetEntity)
        1 * securityUtil.validateAccess(targetEntity)

        and: "Username change logic"
        1 * refreshTokenRepository.deleteByUser(targetEntity)
        1 * adminMapper.updateRequestToEntity(request, targetEntity) >> targetEntity
        1 * adminRepository.save(targetEntity)
        1 * mailService.sendUpdateMail(targetEntity.email)
    }

    def "deleteAdminById should throw error if admin not found"() {
        given:
        def id = UUID.randomUUID()

        when:
        adminService.deleteAdminById(id)

        then:
        1 * adminRepository.findById(id) >> Optional.empty()
        thrown(ResourceNotFoundException)
    }

    def "deleteAdminById should NOT send delete mail if it is SUPER_ADMIN"() {
        given:
        def id = UUID.randomUUID()
        def admin = new AdminEntity(id: id, role: Role.SUPER_ADMIN)

        when:
        adminService.deleteAdminById(id)

        then:
        1 * adminRepository.findById(id) >> Optional.of(admin)
        1 * securityUtil.validateAccess(admin)
        1 * refreshTokenRepository.deleteByUser(admin)
        1 * adminRepository.delete(admin)
        0 * mailService.sendDeleteMail(_)
    }
}