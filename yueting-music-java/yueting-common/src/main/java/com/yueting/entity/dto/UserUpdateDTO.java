package com.yueting.entity.dto;
import lombok.Data;
@Data
public class UserUpdateDTO {
  private Long id;
  private String nickname;
  private Integer gender;
  private Integer status;
  private String remark;
}
