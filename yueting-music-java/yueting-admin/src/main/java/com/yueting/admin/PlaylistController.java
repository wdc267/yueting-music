package com.yueting.admin;
import com.yueting.common.service.PlaylistService;
import com.yueting.entity.dto.PlaylistQueryDTO;
import com.yueting.entity.dto.PlaylistSaveDTO;
import com.yueting.entity.vo.PlaylistVO;
import com.yueting.entity.vo.Result;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/content/playlist")
public class PlaylistController {
  private final PlaylistService playlistService;
  public PlaylistController(PlaylistService playlistService) { this.playlistService = playlistService; }
  @PostMapping("/list")
  public Result<List<PlaylistVO>> list(@RequestBody PlaylistQueryDTO query) { return Result.success(playlistService.selectList(query)); }
  @GetMapping("/{id}")
  public Result<PlaylistVO> getById(@PathVariable Long id) { PlaylistVO vo = playlistService.getById(id); return vo != null ? Result.success(vo) : Result.error(404, "\u6570\u636e\u4e0d\u5b58\u5728"); }
  @PostMapping("/save")
  public Result<Void> save(@RequestBody PlaylistSaveDTO saveDTO) { playlistService.save(saveDTO); return Result.success(); }
  @PutMapping("/update")
  public Result<Void> update(@RequestBody PlaylistSaveDTO saveDTO) { playlistService.update(saveDTO); return Result.success(); }
  @DeleteMapping("/{id}")
  public Result<Void> delete(@PathVariable Long id) { playlistService.delete(id); return Result.success(); }
}