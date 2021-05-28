package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.UserSubscription;
import com.orbvpn.api.domain.enums.PaymentType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Integer> {

  Optional<UserSubscription> findByPaymentTypeAndPaymentId(PaymentType paymentType, String paymentId);
}
