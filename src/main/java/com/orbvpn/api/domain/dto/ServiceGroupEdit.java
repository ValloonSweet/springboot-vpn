package com.orbvpn.api.domain.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceGroupEdit {
  private String name;
  private String description;
  private String language;
  private int discount;
  private List<Integer> gateways;
  private List<Integer> allowedGeolocations;
  private List<Integer> disAllowedGeolocations;
}
