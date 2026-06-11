package com.yueting.common.service;
import com.yueting.entity.dto.SingerQueryDTO;
import com.yueting.entity.dto.SingerSaveDTO;
import com.yueting.entity.dto.SingerSortDTO;
import com.yueting.entity.vo.SingerVO;
import java.util.List;
public interface SingerService {
  List<SingerVO> selectList(SingerQueryDTO query);
  SingerVO getById(Long id);
  void save(SingerSaveDTO saveDTO);
  void update(SingerSaveDTO saveDTO);
  void delete(Long id);
  void updateSort(List<SingerSortDTO> list);
}