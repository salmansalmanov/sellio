package com.sellio.service.concrete;

import com.sellio.model.entity.ListingEntity;
import com.sellio.model.enums.ListingStatus;
import com.sellio.repository.ListingRepository;
import com.sellio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final ListingRepository listingRepository;
    private final MailService mailService;
    private final RedisTemplate<String, String> redisTemplate;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupInactiveListings() {
        log.info("ActionLog.cleanupInactiveListings.start");
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<ListingEntity> inactiveListings = listingRepository
                .findAllByStatusAndUpdatedAtBefore(ListingStatus.INACTIVE, oneMonthAgo);
        if (!inactiveListings.isEmpty()) {
            for (ListingEntity listing : inactiveListings) {
                redisTemplate.delete("listing_view_count_" + listing.getId());
                redisTemplate.delete("listing_count_" + listing.getOwner().getId());
            }
            listingRepository.deleteAll(inactiveListings);
        }
        log.info("ActionLog.cleanupInactiveListings.end");
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
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
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupExpiredListings() {
        log.info("ActionLog.cleanUpExpiredListings.start");
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<ListingEntity> expiredListings = listingRepository
                .findAllByStatusAndUpdatedAtBefore(ListingStatus.EXPIRED, oneMonthAgo);
        if (!expiredListings.isEmpty()) {
            for (ListingEntity listing : expiredListings) {
                redisTemplate.delete("listing_view_count_" + listing.getId());
                redisTemplate.delete("listing_count_" + listing.getOwner().getId());
            }
            listingRepository.deleteAll(expiredListings);
        }
        log.info("ActionLog.cleanUpExpiredListings.end");
    }

    @Transactional
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupLimitExceededListings() {
        log.info("ActionLog.cleanupLimitExceededListings.start");
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<ListingEntity> limitExceededListings = listingRepository
                .findAllByStatusAndUpdatedAtBefore(ListingStatus.LIMIT_EXCEEDED, oneMonthAgo);
        if (!limitExceededListings.isEmpty()) {
            for (ListingEntity listing : limitExceededListings) {
                redisTemplate.delete("listing_view_count_" + listing.getId());
            }
            listingRepository.deleteAll(limitExceededListings);
        }
        log.info("ActionLog.cleanupLimitExceededListings.end");
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void cleanupOrphanImagesFromCloudinary() {
        log.info("ActionLog.cleanupOrphanImagesFromCloudinary.start");
        cleanupImages("listings");
        cleanupImages("shops");
        cleanupEmptyFolders("listings");
        cleanupEmptyFolders("shops");
        log.info("ActionLog.cleanupOrphanImagesFromCloudinary.end");
    }

    private void cleanupImages(String folder) {
        List<String> publicIds = cloudinaryService.getAllPublicIdsInFolder(folder);
        for (String publicId : publicIds) {
            String[] parts = publicId.split("/");
            if (parts.length > 1) {
                String idAsString = parts[1];
                UUID id = UUID.fromString(idAsString);
                boolean exists = folder.equals("listings") ? listingRepository.existsById(id) :
                        userRepository.existsById(id);
                if (!exists) {
                    log.info("Resource not found with id: {}. Images are deleting", id);
                    cloudinaryService.deleteImage(publicId);
                }
            }
        }
    }

    private void cleanupEmptyFolders(String rootFolder) {
        List<String> folderIds = cloudinaryService.getSubfolderNames(rootFolder);

        for (String id : folderIds) {
            String fullPath = rootFolder + "/" + id;
            try {
                cloudinaryService.deleteFolderOnlyIfEmpty(fullPath);
                log.info("Folder {} has been deleted", fullPath);
            } catch (Exception ignored) {
            }
        }
    }
}
