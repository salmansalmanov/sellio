package com.sellio.service.concrete

import com.sellio.model.entity.CustomerEntity
import com.sellio.model.entity.ListingEntity
import com.sellio.model.entity.UserEntity
import com.sellio.model.enums.ListingStatus
import com.sellio.model.enums.PricingPlan
import com.sellio.model.enums.Role
import com.sellio.repository.ListingRepository
import com.sellio.repository.UserRepository
import com.sellio.util.SecurityUtil
import org.springframework.data.domain.Sort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import spock.lang.Specification
import spock.lang.Subject

class PricingServiceTest extends Specification {
    def securityUtil = Mock(SecurityUtil)
    def userRepository = Mock(UserRepository)
    def listingRepository = Mock(ListingRepository)
    def redisTemplate = Mock(RedisTemplate)
    def valueOperations = Mock(ValueOperations)

    @Subject
    def pricingService = new PricingService(securityUtil, userRepository, listingRepository, redisTemplate)

    def setup() {
        redisTemplate.opsForValue() >> valueOperations
    }

    def "should change plan to PREMIUM and activate limited listings"() {
        given: "A user with STANDARD plan and some limited listings"
        def userId = UUID.randomUUID()
        def user = CustomerEntity.builder()
                .id(userId)
                .pricingPlan(PricingPlan.STANDARD)
                .build()

        def limitedListing = ListingEntity.builder().id(UUID.randomUUID()).status(ListingStatus.LIMIT_EXCEEDED).build()
        def limitedListings = [limitedListing]

        when: "Plan is changed to PREMIUM"
        def result = pricingService.changePlan(PricingPlan.PREMIUM)

        then: "User is fetched and plan is updated"
        1 * securityUtil.getCurrentUser() >> user
        1 * userRepository.save(user) >> { UserEntity u -> u.pricingPlan == PricingPlan.PREMIUM; return u }

        and: "Limited listings are activated"
        1 * listingRepository.findAllByOwnerIdAndStatus(userId, ListingStatus.LIMIT_EXCEEDED) >> limitedListings
        1 * listingRepository.saveAll(limitedListings)

        and: "Redis count is updated"
        1 * listingRepository.countByOwnerIdAndStatus(userId, ListingStatus.ACTIVE) >> 5
        1 * valueOperations.set("listing_count_" + userId, "5")

        expect:
        result.success
        result.message == "Pricing plan changed to PREMIUM"
        limitedListing.status == ListingStatus.ACTIVE
    }

    def "should apply limits when Customer changes plan to STANDARD and exceeds limit"() {
        given: "A customer with 5 active listings (limit is 3)"
        def userId = UUID.randomUUID()
        def user = CustomerEntity.builder()
                .id(userId)
                .role(Role.CUSTOMER)
                .pricingPlan(PricingPlan.PREMIUM)
                .build()

        def activeListings = (1..5).collect {
            ListingEntity.builder().id(UUID.randomUUID()).status(ListingStatus.ACTIVE).build()
        }

        when: "Plan is changed to STANDARD"
        def result = pricingService.changePlan(PricingPlan.STANDARD)

        then: "Security and User checks"
        1 * securityUtil.getCurrentUser() >> user
        1 * userRepository.save(user)

        and: "Listing limits are applied"
        1 * listingRepository.findAllByOwnerIdAndStatus(userId, _ as Sort, ListingStatus.ACTIVE) >> activeListings
        1 * listingRepository.saveAll({ List<ListingEntity> toLimit ->
            toLimit.size() == 2 && toLimit.every { it.status == ListingStatus.LIMIT_EXCEEDED }
        })

        and: "Redis count is updated to limit (3)"
        1 * valueOperations.set("listing_count_" + userId, "3")

        expect:
        result.success
        user.pricingPlan == PricingPlan.STANDARD
    }

    def "should NOT apply limits when active listings are within allowed count"() {
        given: "A customer with only 2 active listings"
        def userId = UUID.randomUUID()
        def user = CustomerEntity.builder().id(userId).role(Role.CUSTOMER).build()
        def activeListings = [Mock(ListingEntity), Mock(ListingEntity)]

        when:
        pricingService.changePlan(PricingPlan.STANDARD)

        then:
        1 * securityUtil.getCurrentUser() >> user
        1 * listingRepository.findAllByOwnerIdAndStatus(userId, _ as Sort, ListingStatus.ACTIVE) >> activeListings
        0 * listingRepository.saveAll(_)
        0 * valueOperations.set(_, _)
    }

    def "should apply 500 limit for non-customer roles (e.g. SHOP) on STANDARD plan"() {
        given: "A shop user"
        def userId = UUID.randomUUID()
        def user = CustomerEntity.builder().id(userId).role(Role.SHOP).build()
        def activeListings = (1..501).collect { ListingEntity.builder().build() }

        when:
        pricingService.changePlan(PricingPlan.STANDARD)

        then:
        1 * securityUtil.getCurrentUser() >> user
        1 * listingRepository.findAllByOwnerIdAndStatus(userId, _ as Sort, ListingStatus.ACTIVE) >> activeListings
        1 * listingRepository.saveAll({ List l -> l.size() == 1 })
        1 * valueOperations.set("listing_count_" + userId, "500")
    }
}