package com.yueting.common.service.impl;
import com.yueting.common.mapper.RecommendMapper;
import com.yueting.common.service.RecommendService;
import com.yueting.entity.dto.RecommendQueryDTO;
import com.yueting.entity.dto.RecommendSaveDTO;
import com.yueting.entity.po.Recommend;
import com.yueting.entity.vo.HomeRecommendVO;
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
  public HomeRecommendVO getHomeRecommend(String sortBy) {
    String safeSortBy = normalizeSortBy(sortBy);
    HomeRecommendVO vo = new HomeRecommendVO();
    vo.setBanners(selectEnabledList("banner", null, 6));
    vo.setHotSingers(selectEnabledList("today", "singer", safeSortBy, 6));
    vo.setHotPlaylists(selectEnabledList("today", "playlist", safeSortBy, 6));
    vo.setHotSongs(selectEnabledList("today", "song", safeSortBy, 8));
    return vo;
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
  private List<RecommendVO> selectEnabledList(String recommendType, String resourceType, int limit) {
    return selectEnabledList(recommendType, resourceType, null, limit);
  }
  private List<RecommendVO> selectEnabledList(String recommendType, String resourceType, String sortBy, int limit) {
    RecommendQueryDTO query = new RecommendQueryDTO();
    query.setRecommendType(recommendType);
    query.setResourceType(resourceType);
    query.setStatus(1);
    query.setSortBy(sortBy);
    return selectList(query).stream().limit(limit).collect(Collectors.toList());
  }
  private String normalizeSortBy(String sortBy) {
    if ("likeCount".equals(sortBy) || "favCount".equals(sortBy)) {
      return sortBy;
    }
    return "playCount";
  }
  private RecommendVO toVO(Recommend r) { RecommendVO vo = new RecommendVO(); BeanUtils.copyProperties(r, vo); return vo; }
}
