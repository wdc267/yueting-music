package com.yueting.entity.po;
import java.time.LocalDateTime;
import lombok.Data;
@Data
public class Playlist {
  private Long id;
  private String name;
  private String cover;
  private String description;
  private Integer status;
  private Integer sortOrder;
  private Integer songCount;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}