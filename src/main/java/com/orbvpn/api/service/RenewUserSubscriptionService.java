package com.orbvpn.api.service;

import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.StripeCustomer;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserSubscription;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.domain.enums.PaymentType;
import com.orbvpn.api.reposiitory.StripeCustomerRepository;
import com.orbvpn.api.reposiitory.UserSubscriptionRepository;
import com.stripe.model.PaymentIntent;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RenewUserSubscriptionService {

  private final PaymentService paymentService;

  private final StripeCustomerRepository stripeCustomerRepository;
  private final UserSubscriptionRepository userSubscriptionRepository;
  private final UserSubscriptionService userSubscriptionService;

  public void renewSubscriptions() {
    LocalDateTime now = LocalDateTime.now();

    List<UserSubscription> subscriptionsToRenew = userSubscriptionRepository
      .getSubscriptionsToRenew(now);

    for (UserSubscription userSubscription : subscriptionsToRenew) {
      renewSubscription(userSubscription);
    }

  }

  public void renewSubscription(UserSubscription userSubscription) {
    try {
      if (userSubscription.getPaymentType() == PaymentType.STRIPE) {
        User user = userSubscription.getUser();
        Group group = userSubscription.getGroup();
        UserSubscription newSubscription = userSubscriptionService
          .createUserSubscription(user, group, PaymentType.STRIPE, PaymentStatus.PENDING,
            null);
        PaymentIntent paymentIntent = paymentService.chargeStripeUserOffSession(user, group.getPrice());
        newSubscription.setPaymentId(paymentIntent.getId());
        newSubscription.setRenewed(true);
        userSubscriptionService.fullFillSubscription(newSubscription);
      }
    } catch (Exception ex) {
      log.error("Couldn't renew user subscription", ex.getMessage());
    }

  }
}
