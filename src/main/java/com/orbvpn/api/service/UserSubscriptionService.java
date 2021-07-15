package com.orbvpn.api.service;

import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.Payment;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserSubscription;
import com.orbvpn.api.domain.enums.GatewayName;
import com.orbvpn.api.reposiitory.GroupRepository;
import com.orbvpn.api.reposiitory.StripeCustomerRepository;
import com.orbvpn.api.reposiitory.UserSubscriptionRepository;
import java.time.LocalDateTime;
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
  private final GroupRepository groupRepository;

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
}

