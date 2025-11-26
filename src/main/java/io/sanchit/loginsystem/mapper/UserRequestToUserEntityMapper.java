package io.sanchit.loginsystem.mapper;

import io.sanchit.loginsystem.dto.UserRequest;
import io.sanchit.loginsystem.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserRequestToUserEntityMapper implements Function<UserRequest, UserEntity> {
  @Override
  public UserEntity apply(UserRequest userRequest) {

    UserEntity userEntity = new UserEntity();

    userEntity.setFullName(userRequest.getFullName());
    userEntity.setUsername(userRequest.getUsername());
    userEntity.setPassword(userRequest.getPassword());
    userEntity.setEmail(userRequest.getEmail());
    userEntity.setPhone(userRequest.getPhone());
    userEntity.setAddress(userRequest.getAddress());

    return userEntity;
  }
}