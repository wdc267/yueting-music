package com.yueting.entity.vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserVO {
  private Long id;
  private String username;
  private String nickname;
  private String avatar;
  private Integer gender;
  private Integer status;
  private LocalDateTime lastLoginTime;
  private LocalDateTime createTime;
}