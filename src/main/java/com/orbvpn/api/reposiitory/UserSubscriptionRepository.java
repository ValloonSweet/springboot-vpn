package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.Reseller;
import com.orbvpn.api.domain.entity.UserSubscription;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.domain.enums.PaymentType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Integer> {

  Optional<UserSubscription> findByPaymentTypeAndPaymentId(PaymentType paymentType, String paymentId);

  long countByPaymentStatusAndExpiresAtAfterAndUser_Reseller(PaymentStatus paymentStatus,
    LocalDateTime expiresAt, Reseller reseller);

  @Query("select sum(sub.price) from UserSubscription sub where sub.createdAt > :createdAt and sub.paymentStatus = :paymentStatus")
  BigDecimal getTotalSubscriptionPrice(LocalDateTime createdAt, PaymentStatus paymentStatus);
}