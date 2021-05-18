package com.orbvpn.api.domain.dto;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResellerEdit {
  @Email
  private String email;

  @NotBlank
  private String firstName;

  @NotBlank
  private String lastName;

  @DecimalMin(value = "0.0", inclusive = false)
  private BigDecimal credit;

  private String phone;
}
