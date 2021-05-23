package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.StripeCreatePaymentIntentResponse;
import com.orbvpn.api.domain.dto.StripeCreatePaymentIntent;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

  @Value("stripe.api.public-key")
  private String STRIPE_PUBLIC_KEY;
  @Value("stripe.api.secret-key")
  private String STRIPE_SECRET_KEY;

  @PostConstruct
  public void init() {
    Stripe.apiKey = STRIPE_SECRET_KEY;
  }

  public StripeCreatePaymentIntentResponse stripeCreatePaymentIntent(StripeCreatePaymentIntent charge)
    throws StripeException {
    PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
      .setCurrency("usd")
      .setAmount(1999L)
      .build();
    // Create a PaymentIntent with the order amount and currency
    PaymentIntent intent = PaymentIntent.create(createParams);


    StripeCreatePaymentIntentResponse stripeCreatePaymentIntentResponse = new StripeCreatePaymentIntentResponse();
    stripeCreatePaymentIntentResponse.setClientSecret(intent.getClientSecret());
    return stripeCreatePaymentIntentResponse;
  }
}
