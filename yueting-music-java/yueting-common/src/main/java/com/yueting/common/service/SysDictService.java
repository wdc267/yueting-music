package com.yueting.common.service;
import com.yueting.entity.dto.SysDictSaveDTO;
import com.yueting.entity.vo.SysDictVO;
import java.util.List;
public interface SysDictService {
  List<SysDictVO> listTypes();
  List<SysDictVO> listByCode(String dictCode);
  SysDictVO getById(Long id);
  void save(SysDictSaveDTO saveDTO);
  void update(SysDictSaveDTO saveDTO);
  void delete(Long id);
}