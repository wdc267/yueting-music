package com.yueting.entity.vo;

import java.util.List;
import lombok.Data;

@Data
public class HomeRecommendVO {
  private List<RecommendVO> banners;
  private List<RecommendVO> hotSingers;
  private List<RecommendVO> hotPlaylists;
  private List<RecommendVO> hotSongs;
}
