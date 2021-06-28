package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.StripeCreatePaymentIntentResponse;
import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.Payment;
import com.orbvpn.api.domain.entity.StripeCustomer;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.enums.GatewayName;
import com.orbvpn.api.domain.enums.PaymentCategory;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.reposiitory.PaymentRepository;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeService {
  @Value("${stripe.api.public-key}")
  private String STRIPE_PUBLIC_KEY;
  @Value("${stripe.api.secret-key}")
  private String STRIPE_SECRET_KEY;

  @PostConstruct
  public void init() {
    Stripe.apiKey = STRIPE_SECRET_KEY;
  }

  private final StripeCustomerRepository stripeCustomerRepository;
  private final PaymentRepository paymentRepository;

  public StripeCreatePaymentIntentResponse createStripePayment(Payment payment, User user) throws StripeException {

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

    BigDecimal price = payment.getPrice().multiply(new BigDecimal(100));

    Builder builder = new Builder()
      .setCurrency("usd")
      .setAmount(price.longValue());

    if (payment.isRenew()) {
      builder.setCustomer(stripeCustomer.getStripeId());
      builder.setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION);
    }

    PaymentIntentCreateParams createParams = builder.build();
    // Create a PaymentIntent with the order amount and currency
    PaymentIntent intent = PaymentIntent.create(createParams);

    payment.setPaymentId(intent.getId());
    paymentRepository.save(payment);

    StripeCreatePaymentIntentResponse stripeCreatePaymentIntentResponse = new StripeCreatePaymentIntentResponse();
    stripeCreatePaymentIntentResponse.setClientSecret(intent.getClientSecret());
    return stripeCreatePaymentIntentResponse;
  }

  public PaymentIntent renewStripePayment(Payment payment) throws Exception {
    User user = payment.getUser();
    BigDecimal price = payment.getPrice();

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
}
