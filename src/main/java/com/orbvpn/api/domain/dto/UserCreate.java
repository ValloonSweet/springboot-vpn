package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserCreate {
  private String email;
  private String password;
  private String firstName;
  private String lastName;
}
