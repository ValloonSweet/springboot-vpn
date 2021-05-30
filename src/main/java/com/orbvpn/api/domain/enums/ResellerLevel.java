package com.orbvpn.api.domain.enums;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public enum ResellerLevel {
  STARTER(0, 0),
  BRONZE(5, 5),
  SILVER(15, 15),
  GOLD(30, 30),
  DIAMOND(50, 50),
  OWNER(100, 100);

  private final BigDecimal discountPercent;
  private final BigDecimal minScore;

  ResellerLevel(int discountPercent, int minScore) {
    this.discountPercent = new BigDecimal(discountPercent);
    this.minScore = new BigDecimal(minScore);
  }
}
