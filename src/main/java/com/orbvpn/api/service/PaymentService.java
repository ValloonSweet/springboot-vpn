package com.orbvpn.api.service;

import com.orbvpn.api.config.PayPalClient;
import com.orbvpn.api.domain.dto.StripeCreatePaymentIntentResponse;
import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.StripeCustomer;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserSubscription;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.domain.enums.PaymentType;
import com.orbvpn.api.reposiitory.StripeCustomerRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentCreateParams.Builder;
import com.stripe.param.PaymentMethodListParams;
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
  private final PayPalClient paypalClient;

  private final StripeCustomerRepository stripeCustomerRepository;

  @PostConstruct
  public void init() {
    Stripe.apiKey = STRIPE_SECRET_KEY;
  }

  public StripeCreatePaymentIntentResponse stripeCreatePaymentIntent(int groupId, boolean save)
    throws StripeException {
    Group group = groupService.getById(groupId);
    User user = userService.getUser();

    StripeCustomer stripeCustomer = stripeCustomerRepository.findByUser(user);

    if(stripeCustomer == null) {
      CustomerCreateParams params =
        CustomerCreateParams.builder()
          .build();

      Customer customer = Customer.create(params);
      stripeCustomer = new StripeCustomer();
      stripeCustomer.setUser(user);
      stripeCustomer.setStripeId(customer.getId());
      stripeCustomerRepository.save(stripeCustomer);
    }

    BigDecimal price = group.getPrice().multiply(new BigDecimal(100));

    Builder builder = new Builder()
      .setCurrency(PAYMENT_CURRENCY)
      .setAmount(price.longValue());

    if (save) {
      builder.setCustomer(stripeCustomer.getStripeId());
      builder.setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION);
    }

    PaymentIntentCreateParams createParams = builder.build();
    // Create a PaymentIntent with the order amount and currency
    PaymentIntent intent = PaymentIntent.create(createParams);

    UserSubscription userSubscription = userSubscriptionService
      .createUserSubscription(user, group, PaymentType.STRIPE, PaymentStatus.PENDING,
        intent.getId());

    userSubscription.setRenew(save);

    StripeCreatePaymentIntentResponse stripeCreatePaymentIntentResponse = new StripeCreatePaymentIntentResponse();
    stripeCreatePaymentIntentResponse.setClientSecret(intent.getClientSecret());
    return stripeCreatePaymentIntentResponse;
  }


  public PaymentIntent chargeStripeUserOffSession(User user, BigDecimal price) throws Exception {
    StripeCustomer stripeCustomer = stripeCustomerRepository.findByUser(user);
    BigDecimal priceInCents = price.multiply(new BigDecimal(100));
    String customerId =  stripeCustomer.getStripeId();

    PaymentMethodListParams custParams =
      PaymentMethodListParams.builder()
        .setCustomer(customerId)
        .setType(PaymentMethodListParams.Type.CARD)
        .build();

    PaymentMethodCollection paymentMethods = PaymentMethod.list(custParams);

    String paymentId = paymentMethods.getData().get(0).getId();

    PaymentIntentCreateParams params =
      PaymentIntentCreateParams.builder()
        .setCurrency("usd")
        .setAmount(priceInCents.longValue())
        .setCustomer(customerId)
        .setPaymentMethod(paymentId)
        .setConfirm(true)
        .setOffSession(true)
        .build();

    return PaymentIntent.create(params);
  }

  public void fullFillStripeSubscription(String pid) {
    userSubscriptionService.fullFillSubscription(PaymentType.STRIPE, pid);
  }


}
