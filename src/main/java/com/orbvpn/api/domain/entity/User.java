package com.orbvpn.api.domain.entity;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  @Email
  private String email;

  @Column
  private String password;

  @ManyToOne
  private Role role;

  @Column(nullable = false)
  private String radAccess = "not-a-regular-user";

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private UserProfile profile;

  @ManyToOne
  private Reseller reseller;

  @Column
  @CreatedDate
  private LocalDateTime createdAt;

  @Column
  @LastModifiedDate
  private LocalDateTime updatedAt;

  @Column
  private boolean enabled = true;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(role.getName());
  }

  @Override
  public boolean isAccountNonExpired() {
    return enabled;
  }

  @Override
  public boolean isAccountNonLocked() {
    return enabled;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return enabled;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}
