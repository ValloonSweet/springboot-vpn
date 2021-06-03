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

  @Query("select count(sub.id) from UserSubscription sub where sub.createdAt > :createdAt and sub.paymentStatus = :paymentStatus")
  int countTotalSubscriptionCount(LocalDateTime createdAt, PaymentStatus paymentStatus);

  @Query("select sum(sub.price) from UserSubscription sub where sub.createdAt > :createdAt and sub.paymentStatus = :paymentStatus")
  BigDecimal getTotalSubscriptionPrice(LocalDateTime createdAt, PaymentStatus paymentStatus);

  // Resellers queries

  @Query("select count(sub.id) from UserSubscription sub where sub.expiresAt > current_date and sub.paymentStatus = 'SUCCEEDED' and sub.user.reseller = :reseller")
  BigDecimal countResellerActiveSubscriptions(Reseller reseller);

  @Query("select count(sub.id) from UserSubscription sub where sub.expiresAt > current_date and sub.paymentStatus = 'SUCCEEDED' and sub.user.reseller.level <> 'OWNER'")
  BigDecimal countAllResellersActiveSubscriptions();

  @Query("select sum(sub.price) from UserSubscription sub where sub.createdAt > :createdAt and sub.paymentStatus = 'SUCCEEDED' and sub.user.reseller = :reseller")
  BigDecimal getResellerTotalSale(Reseller reseller, LocalDateTime createdAt);

  @Query("select sum(sub.price) from UserSubscription sub where sub.createdAt > :createdAt and sub.paymentStatus = 'SUCCEEDED' and sub.user.reseller.level <> 'OWNER'")
  BigDecimal getAllResellerTotalSale(LocalDateTime createdAt);
}
