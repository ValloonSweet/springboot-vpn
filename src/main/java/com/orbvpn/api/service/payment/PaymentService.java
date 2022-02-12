package com.orbvpn.api.service.payment;

import com.orbvpn.api.domain.dto.AppleSubscriptionData;
import com.orbvpn.api.domain.dto.ParspalCreatePaymentResponse;
import com.orbvpn.api.domain.dto.PaypalCreatePaymentResponse;
import com.orbvpn.api.domain.dto.StripeCreatePaymentIntentResponse;
import com.orbvpn.api.domain.entity.*;
import com.orbvpn.api.domain.enums.GatewayName;
import com.orbvpn.api.domain.enums.PaymentCategory;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.exception.PaymentException;
import com.orbvpn.api.reposiitory.GroupRepository;
import com.orbvpn.api.reposiitory.MoreLoginCountRepository;
import com.orbvpn.api.reposiitory.PaymentRepository;
import com.orbvpn.api.reposiitory.UserSubscriptionRepository;
import com.orbvpn.api.service.*;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {

  private final StripeService stripeService;
  private final PaypalService paypalService;
  private final AppleService appleService;
  private final UserSubscriptionService userSubscriptionService;
  private final RadiusService radiusService;
  private final GroupService groupService;
  private final ParspalService parspalService;
  @Setter
  private UserService userService;

  private final GroupRepository groupRepository;
  private final PaymentRepository paymentRepository;
  private final UserSubscriptionRepository userSubscriptionRepository;
  private final MoreLoginCountRepository moreLoginCountRepository;

  public void deleteUserPayments(User user) {
    paymentRepository.deleteByUser(user);
  }

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

      // For apple we are using expiresAt from api
      if(payment.getExpiresAt() == null) {
        payment.setExpiresAt(LocalDateTime.now().plusDays(group.getDuration()));
      }

      userSubscriptionService.createUserSubscription(payment, group);
    } else if (payment.getCategory() == PaymentCategory.MORE_LOGIN) {
      User user = payment.getUser();
      UserSubscription userSubscription = userSubscriptionService.getCurrentSubscription(user);
      LocalDateTime expiresAt = userSubscription.getExpiresAt();

      int multiLoginCount = userSubscription.getMultiLoginCount() + payment.getMoreLoginCount();
      userSubscription.setMultiLoginCount(multiLoginCount);
      userSubscriptionRepository.save(userSubscription);
      radiusService.addUserMoreLoginCount(user, multiLoginCount);
      payment.setExpiresAt(expiresAt);

      MoreLoginCount moreLoginCountEntity = new MoreLoginCount();
      moreLoginCountEntity.setUser(user);
      moreLoginCountEntity.setExpiresAt(expiresAt);
      moreLoginCountEntity.setNumber(payment.getMoreLoginCount());
      moreLoginCountRepository.save(moreLoginCountEntity);
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
    } else if(payment.getGateway() == GatewayName.APPLE) {
      AppleSubscriptionData subscriptionData = appleService
        .getSubscriptionData(payment.getMetaData());
      payment.setPaymentId(subscriptionData.getOriginalTransactionId());
      payment.setExpiresAt(subscriptionData.getExpiresAt());
      payment.setMetaData(payment.getMetaData());
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

  public PaypalCreatePaymentResponse paypalCreatePayment(PaymentCategory category, int groupId, int moreLoginCount) throws Exception {
    Payment payment = createPayment(GatewayName.PAYPAL, category, groupId, moreLoginCount, false);
    return paypalService.createPayment(payment);
  }

  public boolean appleCreatePayment(String receipt) {
    log.info("Creating payment for apple");

    AppleSubscriptionData appleSubscriptionData = appleService.getSubscriptionData(receipt);
    Payment payment = createPayment(GatewayName.APPLE, PaymentCategory.GROUP, appleSubscriptionData.getGroupId(), 0, true);
    payment.setPaymentId(appleSubscriptionData.getOriginalTransactionId());
    payment.setMetaData(receipt);
    payment.setExpiresAt(appleSubscriptionData.getExpiresAt());
    fullFillPayment(payment);


    log.info("Created payment for apple receipt : {}", payment);

    return true;
  }

  public boolean paypalApprovePayment(String orderId) throws Exception {
    boolean approved = paypalService.approvePayment(orderId);
    if (approved) {
      fullFillPayment(GatewayName.PAYPAL, orderId);
    }

    return approved;
  }

  public ParspalCreatePaymentResponse parspalCreatePayment(PaymentCategory category, int groupId,
    int moreLoginCount) {
    Payment payment = createPayment(GatewayName.PARSPAL, category, groupId, moreLoginCount, false);
    return parspalService.createPayment(payment);
  }

  public boolean parspalApprovePayment(String payment_id, String receipt_number) {
    boolean approved = parspalService.approvePayment(payment_id, receipt_number);
    if (approved) {
      fullFillPayment(GatewayName.PARSPAL, payment_id);
    }
    return approved;
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
    LocalDateTime now = LocalDateTime.now();
    User user = userService.getUser();
    UserSubscription subscription = userSubscriptionService.getCurrentSubscription(user);
    Group group = subscription.getGroup();
    ServiceGroup serviceGroup = group.getServiceGroup();

    BigDecimal groupPrice = group.getPrice();

    BigDecimal serviceGroupDiscountMultiplier = BigDecimal.ONE
      .subtract(serviceGroup.getDiscount().divide(new BigDecimal(100)));

    LocalDateTime expiresAt = subscription.getExpiresAt();
    BigDecimal expirationDays  = new BigDecimal(DAYS.between(now, expiresAt));

    BigDecimal accountDays = new BigDecimal(DAYS.between(user.getCreatedAt(), now));

   BigDecimal multiLoginPrice = new BigDecimal(number).multiply(groupPrice).multiply(serviceGroupDiscountMultiplier).multiply(expirationDays).divide(accountDays);

    return multiLoginPrice;
  }


}