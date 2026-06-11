package com.yueting.common.mapper;
import com.yueting.entity.dto.PlaylistQueryDTO;
import com.yueting.entity.po.Playlist;
import com.yueting.entity.po.PlaylistSong;
import org.apache.ibatis.annotations.Param;
import java.util.List;
public interface PlaylistMapper {
  List<Playlist> selectList(PlaylistQueryDTO query);
  Playlist selectById(@Param("id") Long id);
  int insert(Playlist playlist);
  int update(Playlist playlist);
  int deleteById(@Param("id") Long id);
  List<PlaylistSong> selectSongsByPlaylistId(@Param("playlistId") Long playlistId);
  int insertPlaylistSong(PlaylistSong ps);
  int deleteSongsByPlaylistId(@Param("playlistId") Long playlistId);
}