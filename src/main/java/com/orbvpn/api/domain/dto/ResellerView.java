package com.orbvpn.api.domain.dto;

import com.orbvpn.api.domain.enums.ResellerLevel;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResellerView {
  private int id;
  private String email;
  private String firstName;
  private String lastName;
  private BigDecimal credit;
  private ResellerLevel level;
  private String phone;
  private Set<ServiceGroupView> serviceGroups;
}
