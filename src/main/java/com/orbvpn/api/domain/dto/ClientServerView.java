package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientServerView {
  private int id;
  private String hostName;
  private String publicIp;
  private String city;
  private String country;
  private String description;
  private String continent;
  private int connectedUserCount;
  private String congestionLevel;
}
