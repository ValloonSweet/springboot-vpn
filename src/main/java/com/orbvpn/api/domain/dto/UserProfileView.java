package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserProfileView {
  private String firstName;
  private String lastName;
  private String phone;
  private String address;
  private String city;
  private String country;
  private String postalCode;
}
