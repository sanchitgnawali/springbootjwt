package io.sanchit.loginsystem.service;

import io.sanchit.loginsystem.entity.auth.RefreshToken;
import io.sanchit.loginsystem.repository.RefreshTokenRepository;
import io.sanchit.loginsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  public RefreshTokenService(
      RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.userRepository = userRepository;
  }

  public RefreshToken generateRefreshToken(String username) {
    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setExpiryDate(Instant.now().plusSeconds(60 * 60 * 24 * 7));
    refreshToken.setToken(UUID.randomUUID().toString());
    refreshToken.setRevoked(false);
    refreshToken.setUser(userRepository.findByUsername(username).get());

    return refreshTokenRepository.save(refreshToken);
  }

  public Optional<RefreshToken> findByToken(String refreshToken) {
    return refreshTokenRepository.findByToken(refreshToken);
  }

  public boolean isRefreshTokenValid(RefreshToken refreshToken) {
    if (refreshToken.getExpiryDate().isAfter(Instant.now()) && !refreshToken.getRevoked()) {
      return true;
    }
    revokeRefreshToken(refreshToken);
    throw new RuntimeException("Refresh token invalid");
  }

  private void revokeRefreshToken(RefreshToken refreshToken) {
    refreshToken.setRevoked(true);
    refreshTokenRepository.save(refreshToken);
  }
}
