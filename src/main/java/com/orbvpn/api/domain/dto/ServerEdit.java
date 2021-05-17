package com.orbvpn.api.domain.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerEdit {
  @NotBlank
  private String hostName;
  private String publicIp;
  private boolean isIbs;
  private String privateIp;
  private String city;
  private String country;
  private String secret;
  private Integer ports;
  private String sshUsername;
  private String sshKey;
  private String killCommand;
  private String rootCommand;
  private String description;
}
