package io.sanchit.loginsystem.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
  private Long id;
  private String fullName;
  private String username;
  private String email;
  private String phone;
  private String address;
}
