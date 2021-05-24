package com.orbvpn.api.domain.dto;

import com.orbvpn.api.domain.enums.ServerType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerEdit {
  @NotBlank
  private String hostName;
  @NotBlank
  private String publicIp;
  @NotNull
  private ServerType type;
  private String privateIp;
  private String city;
  private String country;
  @NotBlank
  private String secret;
  private Integer ports;
  private String sshUsername;
  private String sshKey;
  private String killCommand;
  private String rootCommand;
  private String description;
}
