package com.yueting.entity.dto;
import lombok.Data;
@Data
public class RecommendSaveDTO {
  private Long id;
  private String title;
  private String cover;
  private String resourceType;
  private Long resourceId;
  private String recommendType;
  private Integer status;
  private Integer sortOrder;
}