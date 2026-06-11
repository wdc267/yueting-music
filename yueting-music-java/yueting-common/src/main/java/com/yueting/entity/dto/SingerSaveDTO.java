package com.yueting.entity.dto;
import lombok.Data;
@Data
public class SingerSaveDTO {
  private Long id;
  private String name;
  private String avatar;
  private String region;
  private Integer status;
  private String remark;
  private Integer sortOrder;
}