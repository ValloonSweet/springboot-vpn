package com.orbvpn.api.service;

import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserSubscription;
import com.orbvpn.api.reposiitory.UserSubscriptionRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSubscriptionService {

  private final RadiusService radiusService;
  private final UserSubscriptionRepository userSubscriptionRepository;

  @Transactional
  public UserSubscription createUserSubscription(User user, Group group) {
    log.info("Creating subscription for user with id {} for group {}", user.getId(), group.getId());
    UserSubscription userSubscription = new UserSubscription();

    userSubscription.setUser(user);
    userSubscription.setGroupId(group.getId());
    userSubscription.setDuration(group.getDuration());
    userSubscription.setDailyBandwidth(group.getDailyBandwidth());
    userSubscription.setDownloadUpload(group.getDownloadUpload());
    userSubscription.setMultiLoginCount(group.getMultiLoginCount());
    userSubscription.setExpiresAt(LocalDateTime.now().plusDays(group.getDuration()));

    radiusService.createUserRadChecks(userSubscription);
    userSubscriptionRepository.save(userSubscription);

    return userSubscription;
  }
}

