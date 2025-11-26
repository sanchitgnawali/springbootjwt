package io.sanchit.loginsystem.service;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

@Service
public class JWTService {

  private String secretKey = "";

  public JWTService() {
    try {
      KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
      SecretKey key = keyGen.generateKey();
      secretKey = Base64.getEncoder().encodeToString(key.getEncoded());

    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
    Claims claims = extractClaims(token);

    return claimsResolver.apply(claims);
  }

  private Claims extractClaims(String token) {
    return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
  }

  public String generateToken(String username) {

    Map<String, Object> claims = new HashMap<String, Object>();

    return Jwts.builder()
        .claims()
        .add(claims)
        .subject(username)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5))
        .and()
        .signWith(getKey())
        .compact();
  }

  private SecretKey getKey() {
    byte[] encoded = Base64.getEncoder().encode(secretKey.getBytes());
    return Keys.hmacShaKeyFor(encoded);
  }

  private boolean isTokenExpired(String token) {
    Date expiration = extractClaims(token, Claims::getExpiration);
    return expiration.before(new Date());
  }

  public String extractUsername(String token) {
    return extractClaims(token, Claims::getSubject);
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }
}
