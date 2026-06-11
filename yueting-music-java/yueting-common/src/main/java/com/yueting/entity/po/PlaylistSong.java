package com.yueting.entity.po;
import lombok.Data;
@Data
public class PlaylistSong {
  private Long id;
  private Long playlistId;
  private Long songId;
  private Integer sortOrder;
}