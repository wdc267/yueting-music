package com.yueting.common.service;
import com.yueting.entity.dto.SongQueryDTO;
import com.yueting.entity.dto.SongSaveDTO;
import com.yueting.entity.vo.SongVO;
import java.util.List;
public interface SongService {
  List<SongVO> selectList(SongQueryDTO query);
  SongVO getById(Long id);
  void save(SongSaveDTO saveDTO);
  void update(SongSaveDTO saveDTO);
  void delete(Long id);
}