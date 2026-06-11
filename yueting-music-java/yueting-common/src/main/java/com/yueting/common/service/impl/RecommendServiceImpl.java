package com.yueting.common.service.impl;
import com.yueting.common.mapper.RecommendMapper;
import com.yueting.common.service.RecommendService;
import com.yueting.entity.dto.RecommendQueryDTO;
import com.yueting.entity.dto.RecommendSaveDTO;
import com.yueting.entity.po.Recommend;
import com.yueting.entity.vo.RecommendVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class RecommendServiceImpl implements RecommendService {
  private final RecommendMapper recommendMapper;
  public RecommendServiceImpl(RecommendMapper recommendMapper) { this.recommendMapper = recommendMapper; }
  @Override
  public List<RecommendVO> selectList(RecommendQueryDTO query) {
    List<Recommend> list = recommendMapper.selectList(query);
    return list.stream().map(this::toVO).collect(Collectors.toList());
  }
  @Override
  public RecommendVO getById(Long id) {
    Recommend r = recommendMapper.selectById(id);
    return r != null ? toVO(r) : null;
  }
  @Override @Transactional
  public void save(RecommendSaveDTO dto) { Recommend rec = new Recommend(); BeanUtils.copyProperties(dto, rec); recommendMapper.insert(rec); }
  @Override @Transactional
  public void update(RecommendSaveDTO dto) { Recommend rec = new Recommend(); BeanUtils.copyProperties(dto, rec); recommendMapper.update(rec); }
  @Override @Transactional
  public void delete(Long id) { recommendMapper.deleteById(id); }
  private RecommendVO toVO(Recommend r) { RecommendVO vo = new RecommendVO(); BeanUtils.copyProperties(r, vo); return vo; }
}