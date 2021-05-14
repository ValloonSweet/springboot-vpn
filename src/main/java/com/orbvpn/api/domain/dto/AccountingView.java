package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountingView {
  private int totalUsers;
  private int joinedByDay;
  private int joinedByMonth;
  private int joinedByYear;
}
