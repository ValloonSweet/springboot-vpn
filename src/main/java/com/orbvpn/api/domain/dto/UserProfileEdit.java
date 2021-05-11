package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserProfileEdit {
  private String fullName;
  private String phone;
  private String address;
  private String city;
  private String country;
  private String postalCode;
}
