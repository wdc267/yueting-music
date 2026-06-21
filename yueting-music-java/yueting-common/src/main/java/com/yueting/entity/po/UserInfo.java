package com.yueting.entity.po;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserInfo {
  private Long id;
  private String username;
  private String nickname;
  private String password;
  private String avatar;
  private Integer gender;
  private Integer status;
  private LocalDateTime lastLoginTime;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}