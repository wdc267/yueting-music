package com.yueting.entity.dto;
import lombok.Data;
@Data
public class UserQueryDTO {
  private String username;
  private String nickname;
  private Integer status;
  private Integer page = 1;
  private Integer pageSize = 10;
  public int getOffset() {
    return (page - 1) * pageSize;
  }
}
