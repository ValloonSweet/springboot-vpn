package com.orbvpn.api.domain.entity;

import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.domain.enums.PaymentType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class UserSubscription {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  private User user;

  @ManyToOne
  private Group group;

  @Column
  @Enumerated(EnumType.STRING)
  private PaymentType paymentType;

  @Column
  @Enumerated(EnumType.STRING)
  private PaymentStatus paymentStatus;

  @Column
  private String paymentId;

  @Column
  private BigDecimal price;

  @Column
  @Positive
  private int duration;

  @Column
  @DecimalMin(value = "0")
  private BigInteger dailyBandwidth;

  @Column
  private int multiLoginCount;

  @Column
  @DecimalMin(value = "0")
  private BigInteger downloadUpload;

  @Column
  private LocalDateTime expiresAt;

  @Column
  private boolean renew;

  @Column
  @CreatedDate
  private LocalDateTime createdAt;

  @Column
  @LastModifiedDate
  private LocalDateTime updatedAt;
}
