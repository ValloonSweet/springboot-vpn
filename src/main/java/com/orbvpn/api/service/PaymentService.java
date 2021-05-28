package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.StripeCreatePaymentIntentResponse;
import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.domain.enums.PaymentType;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

  @Value("${stripe.api.public-key}")
  private String STRIPE_PUBLIC_KEY;
  @Value("${stripe.api.secret-key}")
  private String STRIPE_SECRET_KEY;

  private String PAYMENT_CURRENCY= "usd";

  private final UserService userService;
  private final GroupService groupService;
  private final UserSubscriptionService userSubscriptionService;

  @PostConstruct
  public void init() {
    Stripe.apiKey = STRIPE_SECRET_KEY;
  }

  public StripeCreatePaymentIntentResponse stripeCreatePaymentIntent(int groupId)
    throws StripeException {
    Group group = groupService.getById(groupId);
    User user = userService.getUser();

    BigDecimal price = group.getPrice().multiply(new BigDecimal(100));

    PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
      .setCurrency(PAYMENT_CURRENCY)
      .setAmount(price.longValue())
      .build();
    // Create a PaymentIntent with the order amount and currency
    PaymentIntent intent = PaymentIntent.create(createParams);

    userSubscriptionService
      .createUserSubscription(user, group, PaymentType.STRIPE, PaymentStatus.PENDING, intent.getId());

    StripeCreatePaymentIntentResponse stripeCreatePaymentIntentResponse = new StripeCreatePaymentIntentResponse();
    stripeCreatePaymentIntentResponse.setClientSecret(intent.getClientSecret());
    return stripeCreatePaymentIntentResponse;
  }

  public void fullFillStripeSubscription(String pid) {
    userSubscriptionService.fullFillSubscription(PaymentType.STRIPE, pid);
  }


}
