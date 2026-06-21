package com.yueting.common.service;
import com.yueting.entity.vo.DashboardItemVO;
import com.yueting.entity.vo.DashboardVO;
import java.util.List;
public interface DashboardService {
  DashboardVO statistics();
  List<DashboardItemVO> topSongs();
  List<DashboardItemVO> topSingers();
  List<DashboardItemVO> topPlaylists();
}
