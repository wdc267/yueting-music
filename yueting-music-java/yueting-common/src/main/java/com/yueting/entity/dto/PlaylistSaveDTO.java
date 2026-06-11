package com.yueting.entity.dto;
import java.util.List;
import lombok.Data;
@Data
public class PlaylistSaveDTO {
  private Long id;
  private String name;
  private String cover;
  private String description;
  private Integer status;
  private Integer sortOrder;
  private List<Long> songIds;
}