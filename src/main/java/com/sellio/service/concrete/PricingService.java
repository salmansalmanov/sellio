package com.sellio.service.concrete;

import com.sellio.exception.custom.ResourceNotFoundException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PricingService {
    private final SecurityUtil securityUtil;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public Result changePlan(PricingPlan pricingPlan) {
        String email = securityUtil.getCurrentUsernameOrEmail();
        UserEntity userEntity = userRepository.findByIdentifier(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userEntity.setPricingPlan(pricingPlan);
        userRepository.save(userEntity);

        if (pricingPlan == PricingPlan.STANDARD) {
            List<ListingEntity> listings = listingRepository
                    .findAllByOwnerIdAndStatus(userEntity.getId(), Sort.by("createdAt"), ListingStatus.ACTIVE);
            int usersListingCount = listings.size();
            int allowedListingCount;

            allowedListingCount = userEntity.getRole() == Role.CUSTOMER ? 3 : 500;
            if (usersListingCount > allowedListingCount) {
                for (int i = allowedListingCount; i < usersListingCount; i++) {
                    ListingEntity currentListing = listings.get(i);
                    currentListing.setStatus(ListingStatus.LIMIT_EXCEEDED);
                    currentListing.setExpireDate(LocalDateTime.now().plusMonths(1));
                }
                listingRepository.saveAll(listings);
                redisTemplate.opsForValue().set("listing_count_" + userEntity.getId(), String.valueOf(allowedListingCount));
            }
        } else {
            List<ListingEntity> listings = listingRepository
                    .findAllByOwnerIdAndStatus(userEntity.getId(), ListingStatus.LIMIT_EXCEEDED);
            int size = listings.size();
            if (!listings.isEmpty()) {
                for (ListingEntity listingEntity : listings) {
                    listingEntity.setStatus(ListingStatus.ACTIVE);
                    listingEntity.setExpireDate(LocalDateTime.now().plusMonths(1));
                }
                listingRepository.saveAll(listings);
                redisTemplate.opsForValue().increment("listing_count_" + userEntity.getId(), size);
            }
        }
        return new SuccessResult("Pricing plan changed to " + pricingPlan.name());
    }
}
