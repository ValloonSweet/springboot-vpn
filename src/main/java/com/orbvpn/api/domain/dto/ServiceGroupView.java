package com.orbvpn.api.domain.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceGroupView {
  private int id;
  private String name;
  private String description;
  private String language;
  private int discount;
  private List<GatewayView> gateways;
  private List<GeolocationView> allowedGeolocations;
  private List<GeolocationView> disAllowedGeolocations;
}
