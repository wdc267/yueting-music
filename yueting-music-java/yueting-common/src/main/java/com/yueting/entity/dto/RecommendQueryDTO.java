package com.yueting.entity.dto;
import lombok.Data;
@Data
public class RecommendQueryDTO {
  private String recommendType;
  private String resourceType;
  private Integer status;
}