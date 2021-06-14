package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.UserSubscriptionView;
import com.orbvpn.api.domain.entity.UserSubscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = GroupViewMapper.class)
public interface UserSubscriptionViewMapper {
  UserSubscriptionView toView(UserSubscription userSubscription);
}
