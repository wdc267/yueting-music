package com.yueting.entity.vo;
import java.time.LocalDateTime;
import lombok.Data;
@Data
public class RecommendVO {
  private Long id;
  private String title;
  private String cover;
  private String resourceType;
  private Long resourceId;
  private String resourceName;
  private String resourceCover;
  private String resourceUrl;
  private Integer playCount;
  private Integer likeCount;
  private Integer favCount;
  private String recommendType;
  private Integer status;
  private Integer sortOrder;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}
