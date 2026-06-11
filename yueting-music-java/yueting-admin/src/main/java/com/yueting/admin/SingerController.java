package com.yueting.admin;
import com.yueting.common.service.SingerService;
import com.yueting.common.service.SysDictService;
import com.yueting.entity.dto.SingerQueryDTO;
import com.yueting.entity.dto.SingerSaveDTO;
import com.yueting.entity.dto.SingerSortDTO;
import com.yueting.entity.vo.Result;
import com.yueting.entity.vo.SingerVO;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/content/singer")
public class SingerController {
  private final SingerService singerService;
  private final SysDictService sysDictService;
  public SingerController(SingerService singerService, SysDictService sysDictService) { this.singerService = singerService; this.sysDictService = sysDictService; }
  @PostMapping("/list")
  public Result<List<SingerVO>> list(@RequestBody SingerQueryDTO query) { return Result.success(singerService.selectList(query)); }
  @GetMapping("/{id}")
  public Result<SingerVO> getById(@PathVariable Long id) { SingerVO vo = singerService.getById(id); return vo != null ? Result.success(vo) : Result.error(404, "\u6570\u636e\u4e0d\u5b58\u5728"); }
  @PostMapping("/save")
  public Result<Void> save(@RequestBody SingerSaveDTO saveDTO) { singerService.save(saveDTO); return Result.success(); }
  @PutMapping("/update")
  public Result<Void> update(@RequestBody SingerSaveDTO saveDTO) { singerService.update(saveDTO); return Result.success(); }
  @DeleteMapping("/{id}")
  public Result<Void> delete(@PathVariable Long id) { singerService.delete(id); return Result.success(); }
  @PutMapping("/sort")
  public Result<Void> updateSort(@RequestBody List<SingerSortDTO> list) { singerService.updateSort(list); return Result.success(); }
}