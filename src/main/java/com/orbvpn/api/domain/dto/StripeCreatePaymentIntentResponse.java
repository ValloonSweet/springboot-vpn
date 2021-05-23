package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StripeCreatePaymentIntentResponse {
  private String clientSecret;
}
