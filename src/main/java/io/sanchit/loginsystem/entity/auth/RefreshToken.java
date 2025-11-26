package io.sanchit.loginsystem.entity.auth;

import io.sanchit.loginsystem.entity.UserEntity;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String token;
  private Instant expiryDate;
  private Boolean revoked;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  public RefreshToken() {}

  public RefreshToken(Long id, String token, Instant expiryDate, Boolean revoked, UserEntity user) {
    this.id = id;
    this.token = token;
    this.expiryDate = expiryDate;
    this.revoked = revoked;
    this.user = user;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Instant getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(Instant expiryDate) {
    this.expiryDate = expiryDate;
  }

  public Boolean getRevoked() {
    return revoked;
  }

  public void setRevoked(Boolean revoked) {
    this.revoked = revoked;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }
}
