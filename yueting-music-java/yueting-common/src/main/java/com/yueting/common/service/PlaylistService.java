package com.yueting.common.service;
import com.yueting.entity.dto.PlaylistQueryDTO;
import com.yueting.entity.dto.PlaylistSaveDTO;
import com.yueting.entity.vo.PlaylistVO;
import java.util.List;
public interface PlaylistService {
  List<PlaylistVO> selectList(PlaylistQueryDTO query);
  PlaylistVO getById(Long id);
  void save(PlaylistSaveDTO saveDTO);
  void update(PlaylistSaveDTO saveDTO);
  void delete(Long id);
}