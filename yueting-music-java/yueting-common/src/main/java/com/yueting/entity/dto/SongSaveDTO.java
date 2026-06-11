package com.yueting.entity.dto;
import lombok.Data;
@Data
public class SongSaveDTO {
  private Long id;
  private String name;
  private Long singerId;
  private String cover;
  private String url;
  private String musicType;
  private String musicStyle;
  private Integer duration;
  private String lyric;
  private Integer status;
  private String remark;
  private Integer sortOrder;
}