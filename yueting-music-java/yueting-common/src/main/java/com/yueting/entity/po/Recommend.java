package com.yueting.entity.po;
import java.time.LocalDateTime;
import lombok.Data;
@Data
public class Recommend {
  private Long id;
  private String title;
  private String cover;
  private String resourceType;
  private Long resourceId;
  private String recommendType;
  private Integer status;
  private Integer sortOrder;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}