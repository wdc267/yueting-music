package com.yueting.common.service;

import com.yueting.entity.dto.UserFavoriteDTO;
import com.yueting.entity.dto.UserLikeDTO;
import com.yueting.entity.vo.PlaylistVO;
import com.yueting.entity.vo.SingerVO;
import com.yueting.entity.vo.SongVO;
import com.yueting.entity.vo.UserProfileVO;

import java.util.List;

public interface UserActionService {
  boolean toggleLike(UserLikeDTO dto);
  int getLikeCount(String resourceType, Long resourceId);
  boolean toggleFavorite(UserFavoriteDTO dto);
  int getFavoriteCount(String resourceType, Long resourceId);
  boolean hasLiked(Long userId, String resourceType, Long resourceId);
  boolean hasFavorited(Long userId, String resourceType, Long resourceId);
  UserProfileVO getProfile(Long userId);
  List<SongVO> getLikedSongs(Long userId);
  List<SongVO> getFavoriteSongs(Long userId);
  List<PlaylistVO> getFavoritePlaylists(Long userId);
  List<SingerVO> getFavoriteSingers(Long userId);
}