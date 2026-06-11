package com.yueting.admin;
import com.yueting.common.service.RecommendService;
import com.yueting.entity.dto.RecommendQueryDTO;
import com.yueting.entity.dto.RecommendSaveDTO;
import com.yueting.entity.vo.RecommendVO;
import com.yueting.entity.vo.Result;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/operation/recommend")
public class RecommendController {
  private final RecommendService recommendService;
  public RecommendController(RecommendService recommendService) { this.recommendService = recommendService; }
  @PostMapping("/list")
  public Result<List<RecommendVO>> list(@RequestBody RecommendQueryDTO query) { return Result.success(recommendService.selectList(query)); }
  @GetMapping("/{id}")
  public Result<RecommendVO> getById(@PathVariable Long id) { RecommendVO vo = recommendService.getById(id); return vo != null ? Result.success(vo) : Result.error(404, "\u6570\u636e\u4e0d\u5b58\u5728"); }
  @PostMapping("/save")
  public Result<Void> save(@RequestBody RecommendSaveDTO saveDTO) { recommendService.save(saveDTO); return Result.success(); }
  @PutMapping("/update")
  public Result<Void> update(@RequestBody RecommendSaveDTO saveDTO) { recommendService.update(saveDTO); return Result.success(); }
  @DeleteMapping("/{id}")
  public Result<Void> delete(@PathVariable Long id) { recommendService.delete(id); return Result.success(); }
}