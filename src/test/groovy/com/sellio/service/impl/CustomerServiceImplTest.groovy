package com.sellio.service.impl

import com.sellio.exception.custom.ResourceNotFoundException
import com.sellio.mapper.CustomerMapper
import com.sellio.model.dto.request.CustomerRegisterRequest
import com.sellio.model.dto.request.CustomerUpdateRequest
import com.sellio.model.dto.response.core.CustomerDetailsResponse
import com.sellio.model.entity.CustomerEntity
import com.sellio.model.entity.ListingEntity
import com.sellio.model.enums.UserStatus
import com.sellio.repository.CustomerRepository
import com.sellio.repository.RefreshTokenRepository
import com.sellio.repository.UserRepository
import com.sellio.service.concrete.MailService
import com.sellio.util.SecurityUtil
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification
import spock.lang.Subject

class CustomerServiceImplTest extends Specification {
    def customerMapper = Mock(CustomerMapper)
    def userRepository = Mock(UserRepository)
    def mailService = Mock(MailService)
    def customerRepository = Mock(CustomerRepository)
    def passwordEncoder = Mock(PasswordEncoder)
    def redisTemplate = Mock(RedisTemplate)
    def valueOperations = Mock(ValueOperations)
    def refreshTokenRepository = Mock(RefreshTokenRepository)
    def securityUtil = Mock(SecurityUtil)

    @Subject
    def customerService = new CustomerServiceImpl(
            customerMapper, userRepository, mailService, customerRepository,
            passwordEncoder, redisTemplate, refreshTokenRepository, securityUtil
    )

    def setup() {
        redisTemplate.opsForValue() >> valueOperations
    }

    def "save should register customer and initialize redis counter"() {
        given:
        def request = new CustomerRegisterRequest(email: "salman@beu.edu.az", password: "plainPassword")

        def entity = new CustomerEntity(email: request.email)

        def savedEntity = new CustomerEntity(id: UUID.randomUUID(), email: request.email)

        def response = CustomerDetailsResponse.builder().email(request.email).build()

        when:
        def result = customerService.save(request, null, null)

        then:
        1 * customerMapper.registerRequestToEntity(request) >> entity
        1 * passwordEncoder.encode("plainPassword") >> "encodedPassword"
        1 * userRepository.save({ it.status == UserStatus.ACTIVE }) >> savedEntity
        1 * valueOperations.set("listing_count_" + savedEntity.id, "0")
        1 * mailService.sendRegistrationMail(savedEntity.email)
        1 * customerMapper.toDetailsResponse(savedEntity) >> response

        expect:
        result.success
    }

    def "updateCustomerById should validate access and update entity"() {
        given:
        def id = UUID.randomUUID()
        def request = new CustomerUpdateRequest(firstName: "Salman")
        def existingCustomer = new CustomerEntity(id: id, firstName: "OldName", email: "s@s.com")
        def updatedCustomer = new CustomerEntity(id: id, firstName: "Salman", email: "s@s.com")

        when:
        customerService.updateCustomerById(id, request)

        then:
        1 * customerRepository.findById(id) >> Optional.of(existingCustomer)
        1 * securityUtil.validateAccess(existingCustomer)
        1 * customerMapper.updateRequestToEntity(request, existingCustomer) >> updatedCustomer
        1 * customerRepository.save(updatedCustomer)
        1 * mailService.sendUpdateMail(updatedCustomer.email)
    }

    def "deleteCustomerById should cleanup tokens, redis and delete customer"() {
        given:
        def customerId = UUID.randomUUID()
        def listingId = UUID.randomUUID()
        def listing = new ListingEntity()
        listing.id = listingId

        def customer = new CustomerEntity(id: customerId, email: "delete@me.com")
        customer.listings = [listing]

        when:
        customerService.deleteCustomerById(customerId)

        then:
        1 * customerRepository.findById(customerId) >> Optional.of(customer)
        1 * securityUtil.validateAccess(customer)
        1 * refreshTokenRepository.deleteByUser(customer)

        and: "Cleanup redis for each listing"
        1 * redisTemplate.delete("listing_view_count_" + listingId)
        1 * redisTemplate.delete("listing_count_" + listingId)

        and: "Final deletion"
        1 * customerRepository.delete(customer)
        1 * mailService.sendDeleteMail(customer.email)
    }

    def "getCustomerById should throw exception when not found"() {
        given:
        def id = UUID.randomUUID()

        when:
        customerService.getCustomerById(id)

        then:
        1 * customerRepository.findById(id) >> Optional.empty()
        thrown(ResourceNotFoundException)
    }
}