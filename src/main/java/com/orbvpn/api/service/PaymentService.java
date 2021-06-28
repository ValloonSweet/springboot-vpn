package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.StripeCreatePaymentIntentResponse;
import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.Payment;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserSubscription;
import com.orbvpn.api.domain.enums.GatewayName;
import com.orbvpn.api.domain.enums.PaymentCategory;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.exception.PaymentException;
import com.orbvpn.api.reposiitory.GroupRepository;
import com.orbvpn.api.reposiitory.PaymentRepository;
import com.orbvpn.api.reposiitory.UserSubscriptionRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import java.math.BigDecimal;
import java.text.MessageFormat;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {


  private final StripeService stripeService;
  private final UserSubscriptionService userSubscriptionService;
  private final RadiusService radiusService;
  private final GroupService groupService;
  private final GroupRepository groupRepository;
  private final PaymentRepository paymentRepository;
  private final UserSubscriptionRepository userSubscriptionRepository;
  @Setter
  private UserService userService;

  public void fullFillPayment(GatewayName gateway, String paymentId) {
    Payment payment = paymentRepository
      .findByGatewayAndPaymentId(gateway, paymentId)
      .orElseThrow(() -> new RuntimeException("Payment not found"));

    fullFillPayment(payment);
  }

  public void fullFillPayment(Payment payment) {
    if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
      throw new PaymentException("Payment is already fulfilled");
    }

    if (payment.getCategory() == PaymentCategory.GROUP) {
      Group group = groupRepository.getGroupIgnoreDelete(payment.getGroupId());
      UserSubscription userSubscription = userSubscriptionService
        .createUserSubscription(payment, group);
      payment.setExpiresAt(userSubscription.getExpiresAt());
      paymentRepository.save(payment);
    } else if (payment.getCategory() == PaymentCategory.MORE_LOGIN) {
      User user = payment.getUser();
      UserSubscription userSubscription = userSubscriptionService.getCurrentSubscription(user);
      int multiLoginCount = userSubscription.getMultiLoginCount() + payment.getMoreLoginCount();
      userSubscription.setMultiLoginCount(multiLoginCount);
      userSubscriptionRepository.save(userSubscription);
      radiusService.editUserMultiLoginCount(user, multiLoginCount);
    }

    payment.setStatus(PaymentStatus.SUCCEEDED);
    paymentRepository.save(payment);
  }

  public Payment renewPayment(Payment payment) throws Exception {
    Payment newPayment = Payment.builder()
      .user(payment.getUser())
      .status(PaymentStatus.PENDING)
      .gateway(payment.getGateway())
      .category(payment.getCategory())
      .price(payment.getPrice())
      .groupId(payment.getGroupId())
      .renew(true)
      .renewed(true)
      .build();

    if (payment.getGateway() == GatewayName.STRIPE) {
      PaymentIntent paymentIntent = stripeService.renewStripePayment(newPayment);
      newPayment.setPaymentId(paymentIntent.getId());
    } else {
      throw new PaymentException(
        MessageFormat.format("Not supported ooffline payment %s", payment.getGateway()));
    }

    paymentRepository.save(newPayment);
    fullFillPayment(newPayment);

    return newPayment;
  }


  public StripeCreatePaymentIntentResponse stripeCreatePayment(PaymentCategory category,
    int groupId,
    int moreLoginCount, boolean renew)
    throws StripeException {

    Payment payment = createPayment(GatewayName.STRIPE, category, groupId, moreLoginCount, renew);
    User user = userService.getUser();
    return stripeService.createStripePayment(payment, user);
  }

  public Payment createPayment(GatewayName gateway, PaymentCategory category, int groupId,
    int moreLoginCount, boolean renew) {
    if (category == PaymentCategory.GROUP) {
      return createGroupPayment(groupId, renew, gateway);
    }

    if (category == PaymentCategory.MORE_LOGIN) {
      return createBuyMoreLoginPayment(moreLoginCount, gateway);
    }

    throw new PaymentException("Not supported category");
  }

  public Payment createGroupPayment(int groupId, boolean renew, GatewayName gateway) {
    User user = userService.getUser();
    Group group = groupService.getById(groupId);

    Payment payment = Payment.builder()
      .user(user)
      .status(PaymentStatus.PENDING)
      .gateway(gateway)
      .category(PaymentCategory.GROUP)
      .price(group.getPrice())
      .groupId(groupId)
      .renew(renew)
      .build();

    paymentRepository.save(payment);
    return payment;
  }

  public Payment createBuyMoreLoginPayment(int number, GatewayName gateway) {
    User user = userService.getUser();

    Payment payment = Payment.builder()
      .user(user)
      .status(PaymentStatus.PENDING)
      .gateway(gateway)
      .category(PaymentCategory.MORE_LOGIN)
      .price(getBuyMoreLoginPrice(number))
      .moreLoginCount(number)
      .build();

    paymentRepository.save(payment);
    return payment;
  }

  public BigDecimal getBuyMoreLoginPrice(int number) {
    return new BigDecimal("100");
  }


}
