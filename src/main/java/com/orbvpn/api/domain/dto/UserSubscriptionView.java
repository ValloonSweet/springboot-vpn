package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSubscriptionView {
  private GroupView group;
  private int duration;
  private int multiLoginCount;
  private String expiresAt;
  private String createdAt;
  private String updatedAt;
}
