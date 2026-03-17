package com.sellio.service.impl

import com.sellio.event.ImageUploadEvent
import com.sellio.exception.custom.ResourceNotFoundException
import com.sellio.mapper.ShopMapper
import com.sellio.model.dto.request.ShopRegisterRequest
import com.sellio.model.dto.response.core.ShopDetailsResponse
import com.sellio.model.entity.AddressEntity
import com.sellio.model.entity.ShopEntity
import com.sellio.repository.RefreshTokenRepository
import com.sellio.repository.ShopRepository
import com.sellio.repository.UserRepository
import com.sellio.service.abstraction.AddressService
import com.sellio.service.concrete.CloudinaryService
import com.sellio.service.concrete.MailService
import com.sellio.util.FileUtil
import com.sellio.util.RedisUtil
import com.sellio.util.SecurityUtil
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification
import spock.lang.Subject

class ShopServiceImplTest extends Specification {
    def shopMapper = Mock(ShopMapper)
    def cloudinaryService = Mock(CloudinaryService)
    def userRepository = Mock(UserRepository)
    def mailService = Mock(MailService)
    def addressService = Mock(AddressService)
    def fileUtil = Mock(FileUtil)
    def shopRepository = Mock(ShopRepository)
    def redisTemplate = Mock(RedisTemplate)
    def eventPublisher = Mock(ApplicationEventPublisher)
    def redisUtil = Mock(RedisUtil)
    def passwordEncoder = Mock(PasswordEncoder)
    def refreshTokenRepository = Mock(RefreshTokenRepository)
    def securityUtil = Mock(SecurityUtil)

    @Subject
    def service = new ShopServiceImpl(
            shopMapper, cloudinaryService, userRepository, mailService,
            addressService, fileUtil, shopRepository, redisTemplate,
            eventPublisher, redisUtil, passwordEncoder, refreshTokenRepository, securityUtil
    )

    def "save should successfully register shop and publish upload events"() {
        given:
        def request = new ShopRegisterRequest(
                name: "Techno Store",
                email: "info@techno.az",
                password: "password123",
                placeIds: ["place1"] as Set
        )
        def logo = Mock(MultipartFile)
        def banner = Mock(MultipartFile)
        def encodedPassword = "encoded_hash"
        def shopEntity = new ShopEntity(id: UUID.randomUUID(), email: "info@techno.az")
        def valueOps = Mock(ValueOperations)

        when:
        def result = service.save(request, logo, banner)

        then:
        1 * shopMapper.registerRequestToEntity(request) >> shopEntity
        1 * passwordEncoder.encode(request.password) >> encodedPassword
        1 * addressService.save("place1") >> new AddressEntity()
        1 * userRepository.save(_ as ShopEntity) >> shopEntity
        2 * fileUtil.validateImage(_)
        2 * eventPublisher.publishEvent(_ as ImageUploadEvent)
        2 * redisTemplate.opsForValue() >> valueOps
        2 * valueOps.set(_ as String, _ as String)
        1 * mailService.sendRegistrationMail(shopEntity.email)
        1 * shopMapper.toDetailsResponse(shopEntity) >> ShopDetailsResponse.builder().build()

        expect:
        result.success
    }

    def "getShopById should throw ResourceNotFoundException when shop does not exist"() {
        given:
        def id = UUID.randomUUID()

        when:
        service.getShopById(id)

        then:
        1 * shopRepository.findById(id) >> Optional.empty()
        thrown(ResourceNotFoundException)
    }

    def "deleteShopById should clean up all associated data"() {
        given:
        def shopId = UUID.randomUUID()
        def shopEntity = new ShopEntity(id: shopId, email: "test@shop.com", listings: [])

        when:
        def result = service.deleteShopById(shopId)

        then:
        1 * shopRepository.findById(shopId) >> Optional.of(shopEntity)
        1 * securityUtil.validateAccess(shopEntity)
        1 * refreshTokenRepository.deleteByUser(shopEntity)
        1 * cloudinaryService.forceRemoveFolder("shops/" + shopId)
        1 * shopRepository.deleteById(shopId)
        1 * redisTemplate.delete(shopId.toString())
        1 * mailService.sendDeleteMail(shopEntity.email)

        expect:
        result.success
    }

    def "initializeAddresses should not do anything if placeIds is null"() {
        given:
        def shop = new ShopEntity(addresses: [])

        when:
        service.initializeAddresses(shop, null)

        then:
        0 * addressService.save(_)
        shop.addresses.isEmpty()
    }
}