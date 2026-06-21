package com.yueting.common.mapper;
import com.yueting.entity.vo.DashboardItemVO;
import com.yueting.entity.vo.DashboardVO;
import java.util.List;
public interface DashboardMapper {
  DashboardVO statistics();
  List<DashboardItemVO> topSongs();
  List<DashboardItemVO> topSingers();
  List<DashboardItemVO> topPlaylists();
}
