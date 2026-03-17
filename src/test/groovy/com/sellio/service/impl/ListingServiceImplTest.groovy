package com.sellio.service.impl

import com.sellio.event.ImageUploadEvent
import com.sellio.exception.custom.ListingLimitException
import com.sellio.exception.custom.ResourceNotFoundException
import com.sellio.mapper.ListingMapper
import com.sellio.model.dto.request.ListingCreateRequest
import com.sellio.model.dto.response.core.ListingDetailsResponse
import com.sellio.model.entity.CityEntity
import com.sellio.model.entity.CustomerEntity
import com.sellio.model.entity.ListingEntity
import com.sellio.model.entity.PropertyValueEntity
import com.sellio.model.entity.SubcategoryEntity
import com.sellio.model.enums.PricingPlan
import com.sellio.model.enums.Role
import com.sellio.repository.*
import com.sellio.service.concrete.CloudinaryService
import com.sellio.service.concrete.MailService
import com.sellio.util.FileUtil
import com.sellio.util.RedisUtil
import com.sellio.util.SecurityUtil
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification
import spock.lang.Subject

class ListingServiceImplTest extends Specification {
    def userRepository = Mock(UserRepository)
    def listingMapper = Mock(ListingMapper)
    def cityRepository = Mock(CityRepository)
    def subcategoryRepository = Mock(SubcategoryRepository)
    def listingRepository = Mock(ListingRepository)
    def fileUtil = Mock(FileUtil)
    def mailService = Mock(MailService)
    def eventPublisher = Mock(ApplicationEventPublisher)
    def propertyValueRepository = Mock(PropertyValueRepository)
    def cloudinaryService = Mock(CloudinaryService)
    def redisUtil = Mock(RedisUtil)
    def redisTemplate = Mock(RedisTemplate)
    def valueOperations = Mock(ValueOperations)
    def securityUtil = Mock(SecurityUtil)

    @Subject
    def listingService = new ListingServiceImpl(
            userRepository, listingMapper, cityRepository, subcategoryRepository,
            listingRepository, fileUtil, mailService, eventPublisher,
            propertyValueRepository, cloudinaryService, redisUtil, redisTemplate, securityUtil
    )

    def setup() {
        redisTemplate.opsForValue() >> valueOperations
    }

    def "save should successfully create listing and publish image events"() {
        given:
        def request = new ListingCreateRequest(
                cityId: UUID.randomUUID(),
                subcategoryId: UUID.randomUUID(),
                price: new BigDecimal("100"),
                description: "Test description for listing",
                propertyValueIds: [UUID.randomUUID()]
        )
        def image = Mock(MultipartFile)
        def images = [image]

        def user = new CustomerEntity(email: "salman@beu.edu.az", role: Role.CUSTOMER, pricingPlan: PricingPlan.STANDARD)
        user.id = UUID.randomUUID()

        def city = new CityEntity(name: "Baku")
        def subcategory = new SubcategoryEntity(name: "Laptops", isTitleRequired: false)
        def propertyValues = [new PropertyValueEntity(value: "16GB RAM")]

        def listingEntity = new ListingEntity(subcategory: subcategory, listingProperties: [])
        def savedListing = new ListingEntity(id: UUID.randomUUID(), owner: user, subcategory: subcategory)
        def response = ListingDetailsResponse.builder().title("New listing").build()

        when:
        def result = listingService.save(request, images)

        then:
        1 * securityUtil.getCurrentUser() >> user
        1 * userRepository.findByIdentifier(user.email) >> Optional.of(user)
        1 * listingMapper.createRequestToEntity(request) >> listingEntity
        1 * cityRepository.findById(request.cityId) >> Optional.of(city)
        1 * subcategoryRepository.findById(request.subcategoryId) >> Optional.of(subcategory)
        (0..1) * propertyValueRepository.findAllById(_) >> propertyValues

        and: "Redis interactions with flexible matching"
        1 * valueOperations.increment({ it.startsWith("listing_count_") }) >> 1L
        1 * valueOperations.set({ it.startsWith("listing_view_count_") }, "0")

        and: "Image handling"
        1 * fileUtil.validateImage(image)
        1 * image.getBytes() >> "image-data".getBytes()
        1 * eventPublisher.publishEvent(_ as ImageUploadEvent)

        and: "Persistence and response"
        1 * listingRepository.save(_) >> savedListing
        1 * listingMapper.toDetailsResponse(savedListing) >> response
        1 * mailService.sendListingCreatedMail(user.email)

        expect:
        result.success
    }

    def "delete should cleanup redis and decrement counter"() {
        given:
        def listingId = UUID.randomUUID()
        def ownerId = UUID.randomUUID()
        def owner = new CustomerEntity(id: ownerId)
        def entity = new ListingEntity(id: listingId, owner: owner)

        when:
        def result = listingService.delete(listingId)

        then:
        1 * listingRepository.findById(listingId) >> Optional.of(entity)
        1 * redisTemplate.delete("listing_view_count_" + listingId)
        1 * valueOperations.decrement("listing_count_" + ownerId)
        1 * listingRepository.delete(entity)

        expect:
        result.success
        result.message == "Listing deleted successfully"
    }

    def "getById should throw exception when listing doesn't exist"() {
        given:
        def id = UUID.randomUUID()

        when:
        listingService.getById(id)

        then:
        1 * listingRepository.findById(id) >> Optional.empty()
        thrown(ResourceNotFoundException)
    }
}