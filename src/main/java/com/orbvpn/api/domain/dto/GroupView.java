package com.orbvpn.api.domain.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupView {
  private int id;
  private ServiceGroupView serviceGroup;
  private String name;
  private String description;
  private String tagName;
  private int duration;
  private BigDecimal price;
  private String usernamePostfix;
  private String usernamePostfixId;
  private int dailyBandwidth;
  private int multiLoginCount;
  private int downloadUpload;
}
