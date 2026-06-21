package com.yueting.common.service.impl;
import com.yueting.common.mapper.DashboardMapper;
import com.yueting.common.service.DashboardService;
import com.yueting.entity.vo.DashboardItemVO;
import com.yueting.entity.vo.DashboardVO;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class DashboardServiceImpl implements DashboardService {
  private final DashboardMapper dashboardMapper;
  public DashboardServiceImpl(DashboardMapper dashboardMapper) { this.dashboardMapper = dashboardMapper; }
  @Override public DashboardVO statistics() { return dashboardMapper.statistics(); }
  @Override public List<DashboardItemVO> topSongs() { return dashboardMapper.topSongs(); }
  @Override public List<DashboardItemVO> topSingers() { return dashboardMapper.topSingers(); }
  @Override public List<DashboardItemVO> topPlaylists() { return dashboardMapper.topPlaylists(); }
}
