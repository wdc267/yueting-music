package com.yueting.admin;
import com.yueting.common.service.DashboardService;
import com.yueting.entity.vo.DashboardItemVO;
import com.yueting.entity.vo.DashboardVO;
import com.yueting.entity.vo.Result;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/dashboard")
public class DashboardController {
  private final DashboardService dashboardService;
  public DashboardController(DashboardService dashboardService) { this.dashboardService = dashboardService; }
  @GetMapping("/statistics")
  public Result<DashboardVO> statistics() { return Result.success(dashboardService.statistics()); }
  @GetMapping("/top/songs")
  public Result<List<DashboardItemVO>> topSongs() { return Result.success(dashboardService.topSongs()); }
  @GetMapping("/top/singers")
  public Result<List<DashboardItemVO>> topSingers() { return Result.success(dashboardService.topSingers()); }
  @GetMapping("/top/playlists")
  public Result<List<DashboardItemVO>> topPlaylists() { return Result.success(dashboardService.topPlaylists()); }
}
