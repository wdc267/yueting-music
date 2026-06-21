package com.yueting.entity.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
  private String username;
  private String password;
  private String nickname;
  private Integer gender;
}