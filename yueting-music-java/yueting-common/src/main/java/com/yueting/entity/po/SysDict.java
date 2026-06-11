package com.yueting.entity.po;
import lombok.Data;
@Data
public class SysDict {
  private Long id;private Long parentId;private String dictName;private String dictCode;private String dictLabel;private String dictValue;private Integer status;private String remark;private Integer sortOrder;
}