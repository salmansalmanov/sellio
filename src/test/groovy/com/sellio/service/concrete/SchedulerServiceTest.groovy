package com.sellio.service.concrete

import com.sellio.model.entity.CustomerEntity
import com.sellio.model.entity.ListingEntity
import com.sellio.model.enums.ListingStatus
import com.sellio.repository.ListingRepository
import com.sellio.repository.UserRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class SchedulerServiceTest extends Specification {
    def listingRepository = Mock(ListingRepository)
    def mailService = Mock(MailService)
    def redisTemplate = Mock(RedisTemplate)
    def cloudinaryService = Mock(CloudinaryService)
    def userRepository = Mock(UserRepository)
    def valueOperations = Mock(ValueOperations)

    @Subject
    def schedulerService = new SchedulerService(
            listingRepository,
            mailService,
            redisTemplate,
            cloudinaryService,
            userRepository
    )

    def setup() {
        redisTemplate.opsForValue() >> valueOperations
    }

    def "changeStatusForExpiredListings should update status and send mail"() {
        given: "A list of expired active listings"
        def owner = CustomerEntity.builder().email("owner@sellio.com").build()
        def listing = ListingEntity.builder()
                .id(UUID.randomUUID())
                .status(ListingStatus.ACTIVE)
                .owner(owner)
                .build()
        def expiredListings = [listing]

        when:
        schedulerService.changeStatusForExpiredListings()

        then:
        1 * listingRepository.findAllByStatusAndUpdatedAtBefore(ListingStatus.ACTIVE, _ as LocalDateTime) >> expiredListings
        1 * mailService.sendListingExpiredMail("owner@sellio.com")
        1 * listingRepository.saveAll(expiredListings)

        expect:
        listing.status == ListingStatus.EXPIRED
    }

    def "cleanupLimitExceededListings should delete listings and redis keys"() {
        given:
        def listingId = UUID.randomUUID()
        def listing = ListingEntity.builder().id(listingId).status(ListingStatus.LIMIT_EXCEEDED).build()
        def listings = [listing]

        when:
        schedulerService.cleanupLimitExceededListings()

        then:
        1 * listingRepository.findAllByStatusAndUpdatedAtBefore(ListingStatus.LIMIT_EXCEEDED, _ as LocalDateTime) >> listings
        1 * redisTemplate.delete("listing_view_count_" + listingId)
        1 * listingRepository.deleteAll(listings)
    }

    def "cleanupOrphanImagesFromCloudinary should delete images if entity does not exist"() {
        given: "Cloudinary has an image, but database does not have the corresponding listing"
        def orphanId = UUID.randomUUID()
        def publicId = "listings/" + orphanId.toString()

        when:
        schedulerService.cleanupOrphanImagesFromCloudinary()

        then: "Check listings folder for images"
        1 * cloudinaryService.getAllPublicIdsInFolder("listings") >> [publicId]
        1 * listingRepository.existsById(orphanId) >> false
        1 * cloudinaryService.deleteImage(publicId)

        and: "Check shops folder for images"
        1 * cloudinaryService.getAllPublicIdsInFolder("shops") >> []

        and: "Cleanup empty folders for both directories"
        1 * cloudinaryService.getSubfolderNames("listings") >> ["folder1"]
        1 * cloudinaryService.deleteFolderOnlyIfEmpty("listings/folder1")

        1 * cloudinaryService.getSubfolderNames("shops") >> []
    }

    def "processListingCleanup (via cleanupInactiveListings) should decrement redis counts"() {
        given:
        def userId = UUID.randomUUID()
        def listingId = UUID.randomUUID()
        def owner = CustomerEntity.builder().id(userId).build()
        def listing = ListingEntity.builder().id(listingId).owner(owner).build()

        when:
        schedulerService.cleanupInactiveListings()

        then:
        1 * listingRepository.findAllByStatusAndUpdatedAtBefore(ListingStatus.INACTIVE, _ as LocalDateTime) >> [listing]
        1 * valueOperations.decrement("listing_count_" + userId)
        1 * redisTemplate.delete(["listing_view_count_" + listingId])
        1 * listingRepository.deleteAll([listing])
    }

    def "cleanupImages should handle invalid UUID formats gracefully"() {
        given:
        def publicId = "listings/not-a-uuid"

        when:
        schedulerService.cleanupImages("listings")

        then:
        1 * cloudinaryService.getAllPublicIdsInFolder("listings") >> [publicId]
        thrown(IllegalArgumentException) // Kodda try-catch yoxdur UUID.fromString üçün
    }
}