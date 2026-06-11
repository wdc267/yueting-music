package com.yueting.entity.vo;
import java.time.LocalDateTime;
import lombok.Data;
@Data
public class SingerVO {
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