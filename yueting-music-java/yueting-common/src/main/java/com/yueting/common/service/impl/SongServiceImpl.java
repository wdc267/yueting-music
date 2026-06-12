package com.yueting.common.service.impl;

import com.yueting.common.mapper.SongMapper;
import com.yueting.common.service.SongService;
import com.yueting.entity.dto.SongQueryDTO;
import com.yueting.entity.dto.SongSaveDTO;
import com.yueting.entity.po.Song;
import com.yueting.entity.vo.SongVO;
import com.yueting.common.mapper.SingerMapper;
import com.yueting.entity.po.Singer;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongServiceImpl implements SongService {
  private final SongMapper songMapper;
  private final SingerMapper singerMapper;
  public SongServiceImpl(SongMapper songMapper, SingerMapper singerMapper) { this.songMapper = songMapper; this.singerMapper = singerMapper; }

  @Override
  public List<SongVO> selectList(SongQueryDTO query) {
    return songMapper.selectList(query).stream().map(s -> {
      SongVO vo = toVO(s);
      if (s.getSingerId() != null) { Singer singer = singerMapper.selectById(s.getSingerId()); if (singer != null) vo.setSingerName(singer.getName()); }
      return vo;
    }).collect(Collectors.toList());
  }

  @Override
  public List<SongVO> selectEnabledList() {
    return songMapper.selectEnabledList().stream().map(this::toVO).collect(Collectors.toList());
  }

  @Override
  public SongVO getById(Long id) {
    Song s = songMapper.selectById(id);
    if (s == null) return null;
    SongVO vo = toVO(s);
    if (s.getSingerId() != null) { Singer singer = singerMapper.selectById(s.getSingerId()); if (singer != null) vo.setSingerName(singer.getName()); }
    return vo;
  }

  @Override @Transactional public void save(SongSaveDTO dto) { Song song = new Song(); BeanUtils.copyProperties(dto, song); songMapper.insert(song); }
  @Override @Transactional public void update(SongSaveDTO dto) { Song song = new Song(); BeanUtils.copyProperties(dto, song); songMapper.update(song); }
  @Override @Transactional public void delete(Long id) { songMapper.deleteById(id); }

  private SongVO toVO(Song s) { SongVO vo = new SongVO(); BeanUtils.copyProperties(s, vo); return vo; }
}