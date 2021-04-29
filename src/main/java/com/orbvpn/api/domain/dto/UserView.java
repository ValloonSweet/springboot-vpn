package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class UserView {
  private int id;
  private String email;
  private String firstName;
  private String lastName;
}
