package com.sellio.service.concrete;

import com.sellio.model.entity.ListingEntity;
import com.sellio.model.enums.ListingStatus;
import com.sellio.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final ListingRepository listingRepository;
    private final MailService mailService;
    private final RedisTemplate<String, String> redisTemplate;

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupInactiveListings() {
        log.info("ActionLog.cleanupInactiveListings.start");
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<ListingEntity> inactiveListings = listingRepository
                .findAllByStatusAndUpdatedAtBefore(ListingStatus.INACTIVE, oneMonthAgo);
        if (!inactiveListings.isEmpty()) {
            for (ListingEntity listing : inactiveListings) {
                redisTemplate.delete("listing_view_count_" + listing.getId());
            }
            listingRepository.deleteAll(inactiveListings);
        }
        log.info("ActionLog.cleanupInactiveListings.end");
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void changeStatusForExpiredListings() {
        log.info("ActionLog.changeStatusForExpiredListings.start");
        List<ListingEntity> expiredListings = listingRepository
                .findAllByStatusAndUpdatedAtBefore(ListingStatus.ACTIVE, LocalDateTime.now());

        if (!expiredListings.isEmpty()) {
            for (ListingEntity listing : expiredListings) {
                listing.setStatus(ListingStatus.EXPIRED);
                mailService.sendListingExpiredMail(listing.getOwner().getEmail());
            }
        }
        listingRepository.saveAll(expiredListings);
        log.info("ActionLog.changeStatusForExpiredListings.end");
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupExpiredListings() {
        log.info("ActionLog.cleanUpExpiredListings.start");
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<ListingEntity> expiredListings = listingRepository
                .findAllByStatusAndUpdatedAtBefore(ListingStatus.EXPIRED, oneMonthAgo);
        if (!expiredListings.isEmpty()) {
            for (ListingEntity listing : expiredListings) {
                redisTemplate.delete("listing_view_count_" + listing.getId());
            }
            listingRepository.deleteAll(expiredListings);
        }
        log.info("ActionLog.cleanUpExpiredListings.end");
    }
}
