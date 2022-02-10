package com.orbvpn.api.service;

import com.orbvpn.api.domain.entity.*;
import com.orbvpn.api.reposiitory.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserSubscriptionService {

    private final RadiusService radiusService;
    private final UserSubscriptionRepository userSubscriptionRepository;

    public UserSubscription createUserSubscription(Payment payment, Group group) {
        User user = payment.getUser();

        log.info("Creating subscription for user with id {} for group {}", user.getId(), group.getId());
        UserSubscription userSubscription = new UserSubscription();

        userSubscription.setUser(user);
        userSubscription.setGroup(group);
        userSubscription.setPayment(payment);
        userSubscription.setDuration(group.getDuration());
        userSubscription.setDailyBandwidth(group.getDailyBandwidth());
        userSubscription.setDownloadUpload(group.getDownloadUpload());
        userSubscription.setMultiLoginCount(group.getMultiLoginCount());
        userSubscription.setExpiresAt(payment.getExpiresAt());

        userSubscriptionRepository.save(userSubscription);
        radiusService.deleteUserRadChecks(user);
        radiusService.createUserRadChecks(userSubscription);
        return userSubscription;
    }

    public void deleteUserSubscriptions(User user) {
        userSubscriptionRepository.deleteByUser(user);
    }

    public void updateSubscriptionMultiLoginCount(User user, int multiLoginCount) {
        UserSubscription subscription = getCurrentSubscription(user);
        subscription.setMultiLoginCount(multiLoginCount);
        userSubscriptionRepository.save(subscription);
        radiusService.editUserMoreLoginCount(user, multiLoginCount);
    }

    public UserSubscription getCurrentSubscription(User user) {
        return userSubscriptionRepository.findFirstByUserOrderByCreatedAtDesc(user);
    }

    public List<UserProfile> getUsersExpireBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return userSubscriptionRepository.getUsersExpireBetween(startTime, endTime);
    }

    public List<UserProfile> getUsersExpireAt(LocalDate localDate) {
        LocalDateTime startTime = localDate.atStartOfDay();
        LocalDateTime endTime = localDate.plusDays(1).atStartOfDay();
        return getUsersExpireBetween(startTime, endTime);
    }

    public List<UserProfile> getUsersExpireInNextDays(Integer dayCount) {
        LocalDate localDate = LocalDate.now().plusDays(dayCount);
        return getUsersExpireAt(localDate);
    }

    public List<UserProfile> getUsersExpireInPreviousDays(Integer dayCount) {
        LocalDate localDate = LocalDate.now().minusDays(dayCount);
        return getUsersExpireAt(localDate);
    }
}

