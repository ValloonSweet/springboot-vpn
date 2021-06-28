package com.orbvpn.api.service;

import com.orbvpn.api.domain.entity.Payment;
import com.orbvpn.api.reposiitory.PaymentRepository;
import com.orbvpn.api.reposiitory.StripeCustomerRepository;
import com.orbvpn.api.reposiitory.UserSubscriptionRepository;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RenewUserSubscriptionService {

  private final PaymentService paymentService;

  private final StripeCustomerRepository stripeCustomerRepository;
  private final UserSubscriptionRepository userSubscriptionRepository;
  private final UserSubscriptionService userSubscriptionService;
  private final PaymentRepository paymentRepository;

  public void renewSubscriptions() {
    LocalDateTime now = LocalDateTime.now();

    List<Payment> paymentsToRenew = paymentRepository
      .findAllSubscriptionPaymentsToRenew(now);

    for (Payment payment : paymentsToRenew) {
      renewPayment(payment);
    }

  }

  public void renewPayment(Payment payment) {
    try {
      paymentService.renewPayment(payment);
    } catch (Exception ex) {
      log.error("Couldn't renew user subscription", ex.getMessage());
    }

  }
}
