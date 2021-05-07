package com.orbvpn.api.domain.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GroupEdit {
  private int serviceGroupId;
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
