package io.sanchit.loginsystem.service;

import io.sanchit.loginsystem.entity.UserEntity;
import io.sanchit.loginsystem.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

  private final AuthenticationManager authenticationManager;
  private final JWTService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;

  public UserService(
      AuthenticationManager authenticationManager,
      JWTService jwtService,
      PasswordEncoder passwordEncoder,
      UserRepository userRepository) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
  }

  public List<UserEntity> getUsers() {
    return userRepository.findAll();
  }

  public UserEntity saveUser(UserEntity userEntity) {

    if (userRepository.existsByUsername(userEntity.getUsername())) {
      throw new IllegalArgumentException("Username already registered");
    }

    if (userRepository.existsByEmail(userEntity.getEmail())) {
      throw new IllegalArgumentException("Email already registered");
    }

    userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
    return userRepository.save(userEntity);
  }

  public String verifyLogin(UserEntity userEntity) {
    return jwtService.generateToken(userEntity.getUsername());
  }
}
