package com.yueting.entity.vo;
import lombok.Data;
@Data
public class DashboardVO {
  private Long songCount;
  private Long singerCount;
  private Long playlistCount;
  private Long userCount;
  private Long totalPlayCount;
  private Long totalLikeCount;
  private Long totalFavCount;
}
