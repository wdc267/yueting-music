package com.yueting.common.service;
import com.yueting.entity.dto.RecommendQueryDTO;
import com.yueting.entity.dto.RecommendSaveDTO;
import com.yueting.entity.vo.HomeRecommendVO;
import com.yueting.entity.vo.RecommendVO;
import java.util.List;
public interface RecommendService {
  List<RecommendVO> selectList(RecommendQueryDTO query);
  HomeRecommendVO getHomeRecommend(String sortBy);
  RecommendVO getById(Long id);
  void save(RecommendSaveDTO saveDTO);
  void update(RecommendSaveDTO saveDTO);
  void delete(Long id);
}
