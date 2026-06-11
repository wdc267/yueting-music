package com.yueting.admin;
import com.yueting.common.service.SongService;
import com.yueting.entity.dto.SongQueryDTO;
import com.yueting.entity.dto.SongSaveDTO;
import com.yueting.entity.vo.Result;
import com.yueting.entity.vo.SongVO;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/content/song")
public class SongController {
  private final SongService songService;
  public SongController(SongService songService) { this.songService = songService; }
  @PostMapping("/list")
  public Result<List<SongVO>> list(@RequestBody SongQueryDTO query) { return Result.success(songService.selectList(query)); }
  @GetMapping("/{id}")
  public Result<SongVO> getById(@PathVariable Long id) { SongVO vo = songService.getById(id); return vo != null ? Result.success(vo) : Result.error(404, "\u6570\u636e\u4e0d\u5b58\u5728"); }
  @PostMapping("/save")
  public Result<Void> save(@RequestBody SongSaveDTO saveDTO) { songService.save(saveDTO); return Result.success(); }
  @PutMapping("/update")
  public Result<Void> update(@RequestBody SongSaveDTO saveDTO) { songService.update(saveDTO); return Result.success(); }
  @DeleteMapping("/{id}")
  public Result<Void> delete(@PathVariable Long id) { songService.delete(id); return Result.success(); }
}