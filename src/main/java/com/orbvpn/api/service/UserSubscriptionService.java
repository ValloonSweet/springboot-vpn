package com.orbvpn.api.service;

import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.StripeCustomer;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserSubscription;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.domain.enums.PaymentType;
import com.orbvpn.api.reposiitory.StripeCustomerRepository;
import com.orbvpn.api.reposiitory.UserSubscriptionRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserSubscriptionService {

  private final RadiusService radiusService;
  private final UserSubscriptionRepository userSubscriptionRepository;

  private final StripeCustomerRepository stripeCustomerRepository;

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


  public UserSubscription fullFillSubscription(PaymentType type, String pId) {
    UserSubscription subscription = getSubscription(type, pId);
    return fullFillSubscription(subscription);
  }


  public UserSubscription fullFillSubscription(UserSubscription subscription) {
    if(subscription.getPaymentStatus() == PaymentStatus.SUCCEEDED) {
      return subscription;
    }

    subscription.setPaymentStatus(PaymentStatus.SUCCEEDED);
    if(subscription.getExpiresAt() == null) {
      subscription.setExpiresAt(LocalDateTime.now().plusDays(subscription.getDuration()));
    }

    userSubscriptionRepository.save(subscription);
    radiusService.deleteUserRadChecks(subscription.getUser());
    radiusService.createUserRadChecks(subscription);
    return subscription;
  }

  public void deleteUserSubscriptions(User user) {
    userSubscriptionRepository.deleteByUser(user);
  }

  public void updateSubscriptionMultiLoginCount(User user, int multiLoginCount) {
    UserSubscription subscription = getCurrentSubscription(user);
    subscription.setMultiLoginCount(multiLoginCount);
    userSubscriptionRepository.save(subscription);
    radiusService.editUserMultiLoginCount(user, multiLoginCount);
  }

  public UserSubscription getSubscription(PaymentType type, String pid) {
    return userSubscriptionRepository.findByPaymentTypeAndPaymentId(type, pid)
      .orElseThrow(()->new RuntimeException("Payment not found"));
  }

  public UserSubscription getCurrentSubscription(User user) {
    return userSubscriptionRepository.findFirstByUserOrderByCreatedAtDesc(user);
  }
}

