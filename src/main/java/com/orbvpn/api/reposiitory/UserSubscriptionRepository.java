package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Integer> {

}
