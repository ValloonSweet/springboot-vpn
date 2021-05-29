package com.orbvpn.api.domain.entity;

import com.orbvpn.api.domain.enums.ResellerLevel;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Reseller {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @OneToOne(cascade = CascadeType.ALL)
  private User user;

  @Column
  private BigDecimal credit;

  @Column
  @Enumerated(EnumType.STRING)
  private ResellerLevel level;

  @Column
  private LocalDateTime levelSetDate;

  @Column
  private String phone;

  @Column
  private boolean enabled = true;

  @ManyToMany
  private Set<ServiceGroup> serviceGroups = new HashSet<>();

  @Column
  @CreatedDate
  private LocalDateTime createdAt;

  @Column
  @LastModifiedDate
  private LocalDateTime updatedAt;
}
