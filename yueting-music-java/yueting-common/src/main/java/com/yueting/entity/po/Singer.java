package com.yueting.entity.po;
import java.time.LocalDateTime;
import lombok.Data;
@Data
public class Singer {
  private Long id;
  private String name;
  private String avatar;
  private String region;
  private Integer status;
  private String remark;
  private Integer sortOrder;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}