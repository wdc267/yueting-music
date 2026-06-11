package com.yueting.entity.po;
import java.time.LocalDateTime;
import lombok.Data;
@Data
public class Song {
  private Long id;
  private String name;
  private Long singerId;
  private String cover;
  private String url;
  private String musicType;
  private String musicStyle;
  private Integer duration;
  private String lyric;
  private Integer playCount;
  private Integer likeCount;
  private Integer favCount;
  private Integer status;
  private String remark;
  private Integer sortOrder;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}