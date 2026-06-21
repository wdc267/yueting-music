package com.yueting.entity.vo;
import lombok.Data;
@Data
public class DashboardItemVO {
  private Long id;
  private String name;
  private String cover;
  private String singerName;
  private Long playCount;
  private Long likeCount;
  private Long favCount;
}
