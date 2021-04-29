package com.orbvpn.api.domain.entity;

import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column
  private String email;

  @Column
  private String password;

  @Column
  private String firstName;

  @Column
  private String lastName;

  @Transient
  private boolean isEnabled = true;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return isEnabled;
  }

  @Override
  public boolean isAccountNonLocked() {
    return isEnabled;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return isEnabled;
  }

  @Override
  public boolean isEnabled() {
    return isEnabled;
  }
}
