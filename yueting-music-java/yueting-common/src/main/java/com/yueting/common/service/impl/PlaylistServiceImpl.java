package com.yueting.common.service.impl;

import com.yueting.common.mapper.PlaylistMapper;
import com.yueting.common.mapper.SongMapper;
import com.yueting.common.service.PlaylistService;
import com.yueting.entity.dto.PlaylistQueryDTO;
import com.yueting.entity.dto.PlaylistSaveDTO;
import com.yueting.entity.po.Playlist;
import com.yueting.entity.po.Song;
import com.yueting.entity.po.PlaylistSong;
import com.yueting.entity.vo.PlaylistVO;
import com.yueting.entity.vo.SongVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistServiceImpl implements PlaylistService {
  private final PlaylistMapper playlistMapper;
  private final SongMapper songMapper;
  public PlaylistServiceImpl(PlaylistMapper playlistMapper, SongMapper songMapper) { this.playlistMapper = playlistMapper; this.songMapper = songMapper; }

  @Override public List<PlaylistVO> selectList(PlaylistQueryDTO query) { return playlistMapper.selectList(query).stream().map(this::toVO).collect(Collectors.toList()); }
  @Override public List<PlaylistVO> selectEnabledList() { return playlistMapper.selectEnabledList().stream().map(this::toVO).collect(Collectors.toList()); }

  @Override
  public PlaylistVO getById(Long id) {
    Playlist p = playlistMapper.selectById(id);
    if (p == null) return null;
    PlaylistVO vo = toVO(p);
    List<PlaylistSong> psList = playlistMapper.selectSongsByPlaylistId(id);
    List<SongVO> songVOs = psList.stream().map(ps -> {
      Song s = songMapper.selectById(ps.getSongId());
      if (s == null) return null;
      SongVO sv = new SongVO(); BeanUtils.copyProperties(s, sv);
      return sv;
    }).filter(sv -> sv != null).collect(Collectors.toList());
    vo.setSongs(songVOs);
    vo.setSongCount(songVOs.size());
    return vo;
  }

  @Override @Transactional
  public void save(PlaylistSaveDTO dto) {
    Playlist p = new Playlist(); BeanUtils.copyProperties(dto, p);
    playlistMapper.insert(p);
    if (dto.getSongIds() != null) {
      for (int i = 0; i < dto.getSongIds().size(); i++) {
        PlaylistSong ps = new PlaylistSong(); ps.setPlaylistId(p.getId()); ps.setSongId(dto.getSongIds().get(i)); ps.setSortOrder(i);
        playlistMapper.insertPlaylistSong(ps);
      }
    }
  }

  @Override @Transactional
  public void update(PlaylistSaveDTO dto) {
    Playlist p = new Playlist(); BeanUtils.copyProperties(dto, p);
    playlistMapper.update(p);
    playlistMapper.deleteSongsByPlaylistId(p.getId());
    if (dto.getSongIds() != null) {
      for (int i = 0; i < dto.getSongIds().size(); i++) {
        PlaylistSong ps = new PlaylistSong(); ps.setPlaylistId(p.getId()); ps.setSongId(dto.getSongIds().get(i)); ps.setSortOrder(i);
        playlistMapper.insertPlaylistSong(ps);
      }
    }
  }

  @Override @Transactional public void delete(Long id) { playlistMapper.deleteSongsByPlaylistId(id); playlistMapper.deleteById(id); }

  private PlaylistVO toVO(Playlist p) { PlaylistVO vo = new PlaylistVO(); BeanUtils.copyProperties(p, vo); return vo; }
}