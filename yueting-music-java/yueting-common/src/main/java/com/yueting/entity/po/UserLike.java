package com.yueting.entity.po;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserLike {
  private Long id;
  private Long userId;
  private String resourceType;
  private Long resourceId;
  private LocalDateTime createTime;
}