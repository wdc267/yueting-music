package com.yueting.common.service;
import com.yueting.entity.dto.RecommendQueryDTO;
import com.yueting.entity.dto.RecommendSaveDTO;
import com.yueting.entity.vo.RecommendVO;
import java.util.List;
public interface RecommendService {
  List<RecommendVO> selectList(RecommendQueryDTO query);
  RecommendVO getById(Long id);
  void save(RecommendSaveDTO saveDTO);
  void update(RecommendSaveDTO saveDTO);
  void delete(Long id);
}