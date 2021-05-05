package com.orbvpn.api.domain.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceGroupView {
  private String name;
  private String description;
  private List<GatewayView> gateways;
}
