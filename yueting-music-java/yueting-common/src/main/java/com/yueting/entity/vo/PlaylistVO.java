package com.yueting.entity.vo;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
@Data
public class PlaylistVO {
  private Long id;
  private String name;
  private String cover;
  private String description;
  private Integer status;
  private Integer sortOrder;
  private Integer songCount;
  private List<SongVO> songs;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}