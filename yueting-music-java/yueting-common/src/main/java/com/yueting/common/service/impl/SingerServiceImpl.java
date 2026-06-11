package com.yueting.common.service.impl;
import com.yueting.common.mapper.SingerMapper;
import com.yueting.common.service.SingerService;
import com.yueting.entity.dto.SingerQueryDTO;
import com.yueting.entity.dto.SingerSaveDTO;
import com.yueting.entity.dto.SingerSortDTO;
import com.yueting.entity.po.Singer;
import com.yueting.entity.vo.SingerVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class SingerServiceImpl implements SingerService {
  private final SingerMapper singerMapper;
  public SingerServiceImpl(SingerMapper singerMapper) { this.singerMapper = singerMapper; }
  @Override public List<SingerVO> selectList(SingerQueryDTO query) { return singerMapper.selectList(query).stream().map(this::toVO).collect(Collectors.toList()); }
  @Override public SingerVO getById(Long id) { Singer s = singerMapper.selectById(id); return s != null ? toVO(s) : null; }
  @Override @Transactional public void save(SingerSaveDTO dto) { Singer singer = new Singer(); BeanUtils.copyProperties(dto, singer); singerMapper.insert(singer); }
  @Override @Transactional public void update(SingerSaveDTO dto) { Singer singer = new Singer(); BeanUtils.copyProperties(dto, singer); singerMapper.update(singer); }
  @Override @Transactional public void delete(Long id) { singerMapper.deleteById(id); }
  @Override @Transactional public void updateSort(List<SingerSortDTO> list) { for (SingerSortDTO item : list) { singerMapper.updateSort(item.getId(), item.getSortOrder()); } }
  private SingerVO toVO(Singer s) { SingerVO vo = new SingerVO(); BeanUtils.copyProperties(s, vo); return vo; }
}