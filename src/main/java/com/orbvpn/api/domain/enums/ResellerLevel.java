package com.orbvpn.api.domain.enums;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public enum ResellerLevel {
  BRONZE(5), SILVER(15), GOLD(30), DIAMOND(50), OWNER(100);

  private final BigDecimal discountPercent;


  ResellerLevel(int discountPercent) {
    this.discountPercent = new BigDecimal(discountPercent);
  }
}
