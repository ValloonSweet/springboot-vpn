package com.orbvpn.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class UserProfileEdit {
  private String firstName;
  private String lastName;
  private String phone;
  private String address;
  private String city;
  private String country;
  private String postalCode;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ssz")
  private LocalDateTime birthDate;
}
