package io.sanchit.loginsystem.mapper;

import io.sanchit.loginsystem.dto.UserResponse;
import io.sanchit.loginsystem.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserEntityToUserResponseMapper implements Function<UserEntity, UserResponse> {
  @Override
  public UserResponse apply(UserEntity userEntity) {
    return UserResponse.builder()
        .address(userEntity.getAddress())
        .email(userEntity.getEmail())
        .fullName(userEntity.getFullName())
        .id(userEntity.getId())
        .phone(userEntity.getPhone())
        .username(userEntity.getUsername())
        .build();
  }
}
