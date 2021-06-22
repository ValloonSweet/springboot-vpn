package com.orbvpn.api.controller;

import com.orbvpn.api.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class WebHookController {

  @Value("${stripe.api.webhook-secret}")
  private String STRIPE_SECRET_KEY;

  private final PaymentService paymentService;

  @PostMapping("/appstore/events")
  public ResponseEntity<?> handleAppStoreEvent(HttpServletRequest httpServletRequest) {


    return ResponseEntity.ok().build();
  }

  @PostMapping("/stripe/events")
  public ResponseEntity<?> handleWebHook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
    if (sigHeader == null) {
      return ResponseEntity.badRequest().build();
    }

    Event event;

    try {
      event = Webhook.constructEvent(payload, sigHeader, STRIPE_SECRET_KEY);
    } catch (SignatureVerificationException e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().build();
    }

    EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
    StripeObject stripeObject = null;
    if (dataObjectDeserializer.getObject().isPresent()) {
      stripeObject = dataObjectDeserializer.getObject().get();
    } else {
      // Deserialization failed, probably due to an API version mismatch.
      // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
      // instructions on how to handle this case, or return an error here.
      return ResponseEntity.badRequest().build();
    }

    // Handle the event
    switch (event.getType()) {
      case "payment_intent.succeeded":
        PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
        log.info("Payment for " + paymentIntent.getAmount() + " succeeded.");
        // Then define and call a method to handle the successful payment intent.
        paymentService.fullFillStripeSubscription(paymentIntent.getId());
        break;
      default:
        System.out.println("Unhandled event type: " + event.getType());
        break;
    }

    return ResponseEntity.ok().build();
  }
}
