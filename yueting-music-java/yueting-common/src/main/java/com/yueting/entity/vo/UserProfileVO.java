package com.yueting.entity.vo;

import lombok.Data;
import java.util.List;

@Data
public class UserProfileVO {
  private UserVO userInfo;
  private List<SongVO> likedSongs;
  private List<SongVO> favoriteSongs;
  private List<PlaylistVO> favoritePlaylists;
  private List<SingerVO> favoriteSingers;
}