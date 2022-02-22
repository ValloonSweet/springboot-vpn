package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.UserSubscriptionView;
import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.Payment;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserSubscription;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.UserSubscriptionViewMapper;
import com.orbvpn.api.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RenewUserSubscriptionService {

  private final UserSubscriptionViewMapper subscriptionViewMapper;
  private final UserSubscriptionService subscriptionService;
  private final PaymentService paymentService;
  private final RadiusService radiusService;

  public void renewSubscriptions() {

    List<Payment> paymentsToRenew = paymentService.findAllSubscriptionPaymentsToRenew();

    for (Payment payment : paymentsToRenew) {
      renewPayment(payment);
    }
  }

  public Payment renewPayment(Payment payment) {

    try {
      return paymentService.renewPayment(payment);
    } catch (Exception ex) {
      log.error("Couldn't renew user subscription. {}", ex.getMessage());
      return null;
    }
  }

  public UserSubscriptionView renewWithDayCount(User user, Integer days) {

    UserSubscription subscription = subscriptionService.getCurrentSubscription(user);
    subscription.extendDuration(days);
    subscriptionService.saveUserSubscription(subscription);
    radiusService.createUserRadChecks(subscription);

    log.info("The subscription period of User {} has increased by {} days.", user.getId(), days);
    return subscriptionViewMapper.toView(subscription);
  }

  public UserSubscriptionView resetUserSubscription(User user) {

    log.info("User {}'s subscription will reset with the current group.", user.getId());

    Group currentGroup = subscriptionService.getCurrentSubscription(user).getGroup();

    return resetUserSubscription(user, currentGroup.getId());
  }

  public UserSubscriptionView resetUserSubscription(User user, int groupId) {

    log.info("User {}'s subscription will reset with group id {}", user.getId(), groupId);

    UserSubscription subscription = subscriptionService.getCurrentSubscription(user);
    Payment currentPayment = subscription.getPayment();
    if (currentPayment == null) {
      throw new NotFoundException("Subscription of this user does not have a Payment!");
    }

    currentPayment.setGroupId(groupId);
    subscription.setPayment(currentPayment);

    renewPayment(subscription.getPayment());
    UserSubscription newSubscription = subscriptionService.getCurrentSubscription(user);

    return subscriptionViewMapper.toView(newSubscription);
  }
}
