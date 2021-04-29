package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreate {
  private String email;
  private String password;
}
