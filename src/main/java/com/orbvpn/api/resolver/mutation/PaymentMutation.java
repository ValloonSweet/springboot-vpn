package com.orbvpn.api.resolver.mutation;
import com.orbvpn.api.domain.dto.StripeCreatePaymentIntentResponse;
import com.orbvpn.api.service.PaymentService;
import com.stripe.exception.StripeException;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
@Validated
public class PaymentMutation implements GraphQLMutationResolver {

  private final PaymentService paymentService;


  public StripeCreatePaymentIntentResponse stripeCreatePaymentIntent(int  groupId)
    throws StripeException {
    return paymentService.stripeCreatePaymentIntent(groupId);
  }


}
