package com.orbvpn.api.service;

import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserSubscription;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.domain.enums.PaymentType;
import com.orbvpn.api.reposiitory.UserSubscriptionRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
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
  public UserSubscription createUserSubscription(User user, Group group,
    PaymentType type, PaymentStatus status, String pId) {
    log.info("Creating subscription for user with id {} for group {}", user.getId(), group.getId());
    UserSubscription userSubscription = new UserSubscription();

    userSubscription.setUser(user);
    userSubscription.setGroup(group);
    userSubscription.setPrice(group.getPrice());
    userSubscription.setDuration(group.getDuration());
    userSubscription.setDailyBandwidth(group.getDailyBandwidth());
    userSubscription.setDownloadUpload(group.getDownloadUpload());
    userSubscription.setMultiLoginCount(group.getMultiLoginCount());

    userSubscription.setPaymentType(type);
    userSubscription.setPaymentStatus(status);
    userSubscription.setPaymentId(pId);

    userSubscriptionRepository.save(userSubscription);

    return userSubscription;
  }

  @Transactional
  public UserSubscription fullFillSubscription(PaymentType type, String pId) {
    UserSubscription subscription = getSubscription(type, pId);
    return fullFillSubscription(subscription);
  }

  @Transactional
  public UserSubscription fullFillSubscription(UserSubscription subscription) {
    if(subscription.getPaymentStatus() == PaymentStatus.SUCCEEDED) {
      return subscription;
    }

    subscription.setPaymentStatus(PaymentStatus.SUCCEEDED);
    subscription.setExpiresAt(LocalDateTime.now().plusDays(subscription.getDuration()));

    userSubscriptionRepository.save(subscription);
    radiusService.createUserRadChecks(subscription);
    return subscription;
  }

  public UserSubscription getSubscription(PaymentType type, String pid) {
    return userSubscriptionRepository.findByPaymentTypeAndPaymentId(type, pid)
      .orElseThrow(()->new RuntimeException("Payment not found"));
  }
}
