package com.orbvpn.api.domain.entity;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
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

  @ManyToOne (cascade = CascadeType.REMOVE)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @NotFound(action = NotFoundAction.IGNORE)
  private Group group;

  @ManyToOne(fetch = FetchType.LAZY)
  private Payment payment;

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

  // For subscriptions we have expiration data big value
  @Column
  private LocalDateTime expiresAt;


  @Column
  @CreatedDate
  private LocalDateTime createdAt;

  @Column
  @LastModifiedDate
  private LocalDateTime updatedAt;
}
