package com.sellio.service.concrete;

import com.sellio.model.entity.ListingEntity;
import com.sellio.model.entity.UserEntity;
import com.sellio.model.enums.ListingStatus;
import com.sellio.model.enums.PricingPlan;
import com.sellio.model.enums.Role;
import com.sellio.model.result.Result;
import com.sellio.model.result.SuccessResult;
import com.sellio.repository.ListingRepository;
import com.sellio.repository.UserRepository;
import com.sellio.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PricingService {
    private final SecurityUtil securityUtil;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public Result changePlan(PricingPlan pricingPlan) {
        UserEntity currentUserEntity = securityUtil.getCurrentUser();
        currentUserEntity.setPricingPlan(pricingPlan);
        userRepository.save(currentUserEntity);

        if (pricingPlan == PricingPlan.STANDARD) {
            applyStandardPlanLimits(currentUserEntity);
        } else {
            activateLimitedListings(currentUserEntity);
        }
        return new SuccessResult("Pricing plan changed to " + pricingPlan.name());
    }

    private void applyStandardPlanLimits(UserEntity userEntity) {
        List<ListingEntity> activeListings = listingRepository
                .findAllByOwnerIdAndStatus(userEntity.getId(), Sort.by("createdAt"), ListingStatus.ACTIVE);

        int allowedLimit = userEntity.getRole() == Role.CUSTOMER ? 3 : 500;
        if (activeListings.size() > allowedLimit) {
            List<ListingEntity> toLimit = activeListings.subList(allowedLimit, activeListings.size());

            toLimit.forEach(listingEntity -> {
                listingEntity.setStatus(ListingStatus.LIMIT_EXCEEDED);
                listingEntity.setExpireDate(LocalDateTime.now().plusMonths(1));
            });
            listingRepository.saveAll(toLimit);
            updateRedisListingCount(userEntity.getId(), allowedLimit);
        }
    }

    private void updateRedisListingCount(UUID userId, int count) {
        redisTemplate.opsForValue().set("listing_count_" + userId, String.valueOf(count));
    }

    private void activateLimitedListings(UserEntity userEntity) {
        List<ListingEntity> limitedListings = listingRepository
                .findAllByOwnerIdAndStatus(userEntity.getId(), ListingStatus.LIMIT_EXCEEDED);

        if (!limitedListings.isEmpty()) {
            limitedListings.forEach(listing -> {
                listing.setStatus(ListingStatus.ACTIVE);
                listing.setExpireDate(LocalDateTime.now().plusMonths(1));
            });
            listingRepository.saveAll(limitedListings);
        }
        long totalActiveCount = listingRepository.countByOwnerIdAndStatus(userEntity.getId(), ListingStatus.ACTIVE);
        updateRedisListingCount(userEntity.getId(), (int) totalActiveCount);
    }
}
