package com.orbvpn.api.domain.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceGroupEdit {
  private String name;
  private String description;
  private List<Integer> gateways;
}
