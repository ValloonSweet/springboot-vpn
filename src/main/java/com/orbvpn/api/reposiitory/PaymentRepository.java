package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.Payment;
import com.orbvpn.api.domain.enums.GatewayName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
  Optional<Payment> findByGatewayAndPaymentId(GatewayName gateway, String paymentId);

  @Query("select payment from Payment payment where payment.renew = true and payment.expiresAt < :dateTime and payment.category = 'GROUP'")
  List<Payment> findAllSubscriptionPaymentsToRenew(LocalDateTime dateTime);

  @Query("select count(payment.id) from Payment payment where payment.createdAt > :createdAt and payment.renewed = true and payment.category = 'GROUP'")
  int getTotalRenewSubscriptionCount(LocalDateTime createdAt);

  @Query("select sum (payment.price) from Payment payment where payment.createdAt > :createdAt and payment.renewed = true and payment.category = 'GROUP'")
  BigDecimal getTotalRenewSubscriptionPrice(LocalDateTime createdAt);
}
