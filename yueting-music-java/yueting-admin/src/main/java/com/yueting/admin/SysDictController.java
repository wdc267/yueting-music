package com.yueting.admin;

import com.yueting.common.service.SysDictService;
import com.yueting.entity.dto.SysDictSaveDTO;
import com.yueting.entity.vo.Result;
import com.yueting.entity.vo.SysDictVO;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/system/dict")
public class SysDictController {

    private final SysDictService sysDictService;

    public SysDictController(SysDictService sysDictService) {
        this.sysDictService = sysDictService;
    }

    @GetMapping("/types")
    public Result<List<SysDictVO>> listTypes() {
        return Result.success(sysDictService.listTypes());
    }

    @GetMapping("/listByCode")
    public Result<List<SysDictVO>> listByCode(@RequestParam String dictCode) {
        return Result.success(sysDictService.listByCode(dictCode));
    }

    @GetMapping("/{id}")
    public Result<SysDictVO> getById(@PathVariable Long id) {
        SysDictVO vo = sysDictService.getById(id);
        return vo != null ? Result.success(vo) : Result.error(404, "数据不存在");
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody SysDictSaveDTO saveDTO) {
        sysDictService.save(saveDTO);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody SysDictSaveDTO saveDTO) {
        sysDictService.update(saveDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysDictService.delete(id);
        return Result.success();
    }
}