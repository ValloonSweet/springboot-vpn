package com.orbvpn.api.domain.dto;

import com.orbvpn.api.domain.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserView {
  private int id;
  private String email;
  private String firstName;
  private String lastName;
  private Role role;
}
