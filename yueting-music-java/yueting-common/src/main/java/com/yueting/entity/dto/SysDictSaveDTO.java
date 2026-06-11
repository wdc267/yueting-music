package com.yueting.entity.dto;
import lombok.Data;
@Data
public class SysDictSaveDTO {
  private Long id;private Long parentId;private String dictName;private String dictCode;private String dictLabel;private String dictValue;private Integer status;private String remark;private Integer sortOrder;
}