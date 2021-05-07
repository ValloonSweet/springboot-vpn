package com.orbvpn.api.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "group_app")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Group {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  private ServiceGroup serviceGroup;

  @Column
  private String name;

  @Column
  private String description;

  @Column
  private String tagName;

  @Column
  private int duration;

  @Column
  private BigDecimal price;

  @Column
  private String usernamePostfix;

  @Column
  private String usernamePostfixId;

  @Column
  private int dailyBandwidth;

  @Column
  private int multiLoginCount;

  @Column
  private int downloadUpload;

  @Column
  @CreatedDate
  private LocalDateTime createdAt;

  @Column
  @LastModifiedDate
  private LocalDateTime updatedAt;
}
