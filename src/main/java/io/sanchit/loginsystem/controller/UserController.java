package io.sanchit.loginsystem.controller;

import io.sanchit.loginsystem.dto.JwtResponse;
import io.sanchit.loginsystem.dto.RefreshTokenRequest;
import io.sanchit.loginsystem.dto.UserRequest;
import io.sanchit.loginsystem.dto.UserResponse;
import io.sanchit.loginsystem.entity.UserEntity;
import io.sanchit.loginsystem.entity.auth.RefreshToken;
import io.sanchit.loginsystem.mapper.UserEntityToUserResponseMapper;
import io.sanchit.loginsystem.mapper.UserRequestToUserEntityMapper;
import io.sanchit.loginsystem.service.JWTService;
import io.sanchit.loginsystem.service.RefreshTokenService;
import io.sanchit.loginsystem.service.UserService;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
  private final AuthenticationManager authenticationManager;
  private final JWTService jwtService;
  private final RefreshTokenService refreshTokenService;
  private final UserEntityToUserResponseMapper userEntityToUserResponseMapper;
  private final UserRequestToUserEntityMapper userRequestToUserEntityMapper;
  private final UserService userService;

  public UserController(
      AuthenticationManager authenticationManager,
      JWTService jwtService,
      RefreshTokenService refreshTokenService,
      UserEntityToUserResponseMapper userEntityToUserResponseMapper,
      UserRequestToUserEntityMapper userRequestToUserEntityMapper,
      UserService userService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.refreshTokenService = refreshTokenService;
    this.userEntityToUserResponseMapper = userEntityToUserResponseMapper;
    this.userRequestToUserEntityMapper = userRequestToUserEntityMapper;
    this.userService = userService;
  }

  @GetMapping("users")
  public ResponseEntity<List<UserResponse>> getUsers() {
    List<UserEntity> users = userService.getUsers();

    List<UserResponse> usersResponse = users.stream().map(userEntityToUserResponseMapper).toList();
    return ResponseEntity.ok(usersResponse);
  }

  @PostMapping("login")
  public ResponseEntity<JwtResponse> login(@RequestBody UserRequest userRequest) {

    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                userRequest.getUsername(), userRequest.getPassword()));

    if (authentication.isAuthenticated()) {
      String accessToken =
          userService.verifyLogin(userRequestToUserEntityMapper.apply(userRequest));
      String refreshToken =
          refreshTokenService.generateRefreshToken(userRequest.getUsername()).getToken();

      ResponseCookie responseCookie =
          ResponseCookie.from("refresh-token", refreshToken)
              .httpOnly(true)
              .path("/")
              .maxAge(Duration.ofDays(7))
              .sameSite("Strict")
              .build();

      return ResponseEntity.ok()
          .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
          .body(new JwtResponse(accessToken));
    }

    throw new UsernameNotFoundException("Invalid username or password");
  }

  @PostMapping("register")
  public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
    UserEntity userEntity = userRequestToUserEntityMapper.apply(userRequest);

    userService.saveUser(userEntity);
    UserResponse userResponse = userEntityToUserResponseMapper.apply(userEntity);
    return ResponseEntity.ok(userResponse);
  }

  @PostMapping("refreshToken")
  public ResponseEntity<JwtResponse> refreshToken(@CookieValue("refresh-token") String token) {

    RefreshToken refreshToken =
        refreshTokenService
            .findByToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

    if (refreshTokenService.isRefreshTokenValid(refreshToken)) {

      refreshTokenService.revokeRefreshToken(refreshToken);

      RefreshToken newRefreshToken =
          refreshTokenService.generateRefreshToken(refreshToken.getUser().getUsername());

      ResponseCookie cookie =
          ResponseCookie.from("refresh-token", newRefreshToken.getToken())
              .httpOnly(true)
              .path("/")
              .maxAge(Duration.ofDays(7))
              .sameSite("Strict")
              .build();

      return ResponseEntity.ok()
          .header(HttpHeaders.SET_COOKIE, cookie.toString())
          .body(new JwtResponse(jwtService.generateToken(refreshToken.getUser().getUsername())));
    }

    throw new RuntimeException("Please login.");
  }
}
