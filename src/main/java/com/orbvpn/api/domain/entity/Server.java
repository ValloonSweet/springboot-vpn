package com.orbvpn.api.domain.entity;

import com.orbvpn.api.domain.enums.ServerType;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Server {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column
  private String hostName;

  @Column(unique = true)
  private String publicIp;

  @Column
  @Enumerated(EnumType.STRING)
  private ServerType type;

  @Column
  private String privateIp;

  @Column
  private String city;

  @Column
  private String country;

  @Column
  private String secret;

  @Column
  private Integer ports;

  @Column
  private String sshUsername;

  @Column
  private String sshKey;

  @Column
  private String killCommand;

  @Column
  private String rootCommand;

  @Column
  private String description;

  @Column
  @CreatedDate
  private LocalDateTime createdAt;

  @Column
  @LastModifiedDate
  private LocalDateTime updatedAt;
}
