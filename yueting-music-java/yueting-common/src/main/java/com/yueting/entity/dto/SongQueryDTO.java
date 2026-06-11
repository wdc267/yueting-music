package com.yueting.entity.dto;
import lombok.Data;
@Data
public class SongQueryDTO {
  private String name;
  private Long singerId;
  private String musicType;
  private Integer status;
}