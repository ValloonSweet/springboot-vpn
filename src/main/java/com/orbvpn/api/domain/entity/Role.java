package com.orbvpn.api.domain.entity;

import com.orbvpn.api.domain.enums.RoleName;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Role {
  @Id
  private int id;

  @Column
  @Enumerated(EnumType.STRING)
  private RoleName name;
}
