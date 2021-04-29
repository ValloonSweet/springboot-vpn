package com.orbvpn.api.domain.dto;

import lombok.Data;

@Data
public class UserCredentials {
  private String email;
  private String password;
}
