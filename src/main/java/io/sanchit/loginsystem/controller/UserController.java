package io.sanchit.loginsystem.controller;

import io.sanchit.loginsystem.dto.UserRequest;
import io.sanchit.loginsystem.dto.UserResponse;
import io.sanchit.loginsystem.entity.UserEntity;
import io.sanchit.loginsystem.mapper.UserEntityToUserResponseMapper;
import io.sanchit.loginsystem.mapper.UserRequestToUserEntityMapper;
import io.sanchit.loginsystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

  private final UserEntityToUserResponseMapper userEntityToUserResponseMapper;
  private final UserRequestToUserEntityMapper userRequestToUserEntityMapper;
  private final UserService userService;

  public UserController(
      UserEntityToUserResponseMapper userEntityToUserResponseMapper,
      UserRequestToUserEntityMapper userRequestToUserEntityMapper,
      UserService userService) {
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
  public String login(@RequestBody UserRequest userRequest) {

    return userService.verifyLogin(userRequestToUserEntityMapper.apply(userRequest));
  }

  @PostMapping("register")
  public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
    UserEntity userEntity = userRequestToUserEntityMapper.apply(userRequest);

    userService.saveUser(userEntity);
    UserResponse userResponse = userEntityToUserResponseMapper.apply(userEntity);
    return ResponseEntity.ok(userResponse);
  }
}
