package com.yueting.entity.vo;
import lombok.Data;
import java.util.List;
@Data
public class SysDictVO {
  private Long id;private Long parentId;private String dictName;private String dictCode;private String dictLabel;private String dictValue;private Integer status;private String remark;private Integer sortOrder;
  private List<SysDictVO> children;
}