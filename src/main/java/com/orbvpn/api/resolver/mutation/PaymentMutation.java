package com.orbvpn.api.resolver.mutation;
import com.orbvpn.api.domain.dto.PaypalCreatePaymentResponse;
import com.orbvpn.api.domain.dto.StripeCreatePaymentIntentResponse;
import com.orbvpn.api.domain.enums.PaymentCategory;
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


  public StripeCreatePaymentIntentResponse stripeCreatePayment(PaymentCategory category,
    int groupId,
    int moreLoginCount, boolean renew)
    throws StripeException {
    return paymentService.stripeCreatePayment(category, groupId, moreLoginCount, renew);
  }

  public PaypalCreatePaymentResponse paypalCreatePayment(PaymentCategory category, int groupId,
    int moreLoginCount)
    throws Exception {
    return paymentService.paypalCreatePayment(category, groupId, moreLoginCount);
  }

  public boolean paypalApprovePayment(String orderId) throws Exception {
    return paymentService.paypalApprovePayment(orderId);
  }

  public boolean appleCreatePayment(String receipt) {
    return paymentService.appleCreatePayment(receipt);
  }

}
