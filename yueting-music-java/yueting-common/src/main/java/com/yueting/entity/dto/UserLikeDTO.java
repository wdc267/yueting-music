package com.yueting.entity.dto;

import lombok.Data;

@Data
public class UserLikeDTO {
  private Long userId;
  private String resourceType;
  private Long resourceId;
}