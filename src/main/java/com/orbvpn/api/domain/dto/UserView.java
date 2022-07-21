package com.orbvpn.api.domain.dto;

import com.orbvpn.api.domain.entity.UserDeviceInfo;
import com.orbvpn.api.domain.enums.RoleName;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserView {
  private int id;
  private String email;
  private RoleName role;
  private String radAccess;
  private String radAccessClear;
  private UserProfileView profile;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private List<UserDeviceInfo> userDevicesInfo;

  private UserSubscriptionView subscription;
  private UserSubscriptionView userSubscription;
}
