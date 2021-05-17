package com.orbvpn.api.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "raccheck")
@Entity
@Getter
@Setter
public class RadCheck {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column
  private String username;

  @Column
  private String attribute;

  @Column
  private String op;

  @Column
  private String value;
}
