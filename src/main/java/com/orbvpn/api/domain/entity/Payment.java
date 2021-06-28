package com.orbvpn.api.domain.entity;

import com.orbvpn.api.domain.enums.GatewayName;
import com.orbvpn.api.domain.enums.PaymentCategory;
import com.orbvpn.api.domain.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  private User user;

  @Column(nullable = false)
  private PaymentStatus status;

  @Column(nullable = false)
  private GatewayName gateway;

  @Column(nullable = false)
  private PaymentCategory category;

  @Column(nullable = false)
  private String paymentId;

  @Column
  @DecimalMin(value = "0.0", inclusive = true)
  private BigDecimal price;

  @Column
  private int groupId;

  @Column
  private int moreLoginCount;

  @Column
  private boolean renew;

  @Column
  private boolean renewed;

  @Column
  private LocalDateTime expiresAt;

  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;
}
