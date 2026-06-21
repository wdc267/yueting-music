package com.yueting.common.service.impl;

import com.yueting.common.mapper.*;
import com.yueting.common.service.UserActionService;
import com.yueting.entity.dto.UserFavoriteDTO;
import com.yueting.entity.dto.UserLikeDTO;
import com.yueting.entity.po.*;
import com.yueting.entity.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserActionServiceImpl implements UserActionService {

  private final UserLikeMapper userLikeMapper;
  private final UserFavoriteMapper userFavoriteMapper;
  private final UserPlayHistoryMapper userPlayHistoryMapper;
  private final UserMapper userMapper;
  private final SongMapper songMapper;
  private final SingerMapper singerMapper;
  private final PlaylistMapper playlistMapper;

  public UserActionServiceImpl(UserLikeMapper userLikeMapper,
                               UserFavoriteMapper userFavoriteMapper,
                               UserPlayHistoryMapper userPlayHistoryMapper,
                               UserMapper userMapper,
                               SongMapper songMapper,
                               SingerMapper singerMapper,
                               PlaylistMapper playlistMapper) {
    this.userLikeMapper = userLikeMapper;
    this.userFavoriteMapper = userFavoriteMapper;
    this.userPlayHistoryMapper = userPlayHistoryMapper;
    this.userMapper = userMapper;
    this.songMapper = songMapper;
    this.singerMapper = singerMapper;
    this.playlistMapper = playlistMapper;
  }

  @Override @Transactional
  public boolean toggleLike(UserLikeDTO dto) {
    UserLike exist = userLikeMapper.selectByUserAndResource(dto.getUserId(), dto.getResourceType(), dto.getResourceId());
    if (exist != null) {
      userLikeMapper.deleteByUserAndResource(dto.getUserId(), dto.getResourceType(), dto.getResourceId());
      updateResourceLikeCount(dto.getResourceType(), dto.getResourceId(), -1);
      return false;
    } else {
      UserLike like = new UserLike();
      like.setUserId(dto.getUserId());
      like.setResourceType(dto.getResourceType());
      like.setResourceId(dto.getResourceId());
      userLikeMapper.insert(like);
      updateResourceLikeCount(dto.getResourceType(), dto.getResourceId(), 1);
      return true;
    }
  }

  @Override public int getLikeCount(String resourceType, Long resourceId) { return userLikeMapper.countByResource(resourceType, resourceId); }

  @Override @Transactional
  public boolean toggleFavorite(UserFavoriteDTO dto) {
    UserFavorite exist = userFavoriteMapper.selectByUserAndResource(dto.getUserId(), dto.getResourceType(), dto.getResourceId());
    if (exist != null) {
      userFavoriteMapper.deleteByUserAndResource(dto.getUserId(), dto.getResourceType(), dto.getResourceId());
      updateResourceFavCount(dto.getResourceType(), dto.getResourceId(), -1);
      return false;
    } else {
      UserFavorite fav = new UserFavorite();
      fav.setUserId(dto.getUserId());
      fav.setResourceType(dto.getResourceType());
      fav.setResourceId(dto.getResourceId());
      userFavoriteMapper.insert(fav);
      updateResourceFavCount(dto.getResourceType(), dto.getResourceId(), 1);
      return true;
    }
  }

  private void updateResourceLikeCount(String resourceType, Long resourceId, int delta) {
    if ("song".equals(resourceType)) {
      songMapper.incrLikeCount(resourceId, delta);
    }
  }

  private void updateResourceFavCount(String resourceType, Long resourceId, int delta) {
    if ("song".equals(resourceType)) {
      songMapper.incrFavCount(resourceId, delta);
    }
  }

  @Override public int getFavoriteCount(String resourceType, Long resourceId) { return userFavoriteMapper.countByResource(resourceType, resourceId); }
  @Override public boolean hasLiked(Long userId, String resourceType, Long resourceId) { return userLikeMapper.selectByUserAndResource(userId, resourceType, resourceId) != null; }
  @Override public boolean hasFavorited(Long userId, String resourceType, Long resourceId) { return userFavoriteMapper.selectByUserAndResource(userId, resourceType, resourceId) != null; }

  @Override
  public void recordPlay(Long songId, Long userId) {
    songMapper.incrPlayCount(songId);
    if (userId != null) {
      UserPlayHistory history = new UserPlayHistory();
      history.setUserId(userId);
      history.setSongId(songId);
      userPlayHistoryMapper.insert(history);
    }
  }

  @Override
  public UserProfileVO getProfile(Long userId) {
    UserProfileVO profile = new UserProfileVO();
    UserInfo user = userMapper.selectById(userId);
    if (user != null) {
      UserVO userVO = new UserVO();
      BeanUtils.copyProperties(user, userVO);
      profile.setUserInfo(userVO);
    }
    profile.setLikedSongs(getLikedSongs(userId));
    profile.setFavoriteSongs(getFavoriteSongs(userId));
    profile.setFavoritePlaylists(getFavoritePlaylists(userId));
    profile.setFavoriteSingers(getFavoriteSingers(userId));
    return profile;
  }

  @Override
  public List<SongVO> getLikedSongs(Long userId) {
    return userLikeMapper.selectByUserIdAndType(userId, "song").stream()
      .map(l -> { Song s = songMapper.selectById(l.getResourceId()); if (s == null) return null; SongVO vo = new SongVO(); BeanUtils.copyProperties(s, vo); return vo; })
      .filter(v -> v != null).collect(Collectors.toList());
  }

  @Override
  public List<SongVO> getFavoriteSongs(Long userId) {
    return userFavoriteMapper.selectByUserIdAndType(userId, "song").stream()
      .map(f -> { Song s = songMapper.selectById(f.getResourceId()); if (s == null) return null; SongVO vo = new SongVO(); BeanUtils.copyProperties(s, vo); return vo; })
      .filter(v -> v != null).collect(Collectors.toList());
  }

  @Override
  public List<PlaylistVO> getFavoritePlaylists(Long userId) {
    return userFavoriteMapper.selectByUserIdAndType(userId, "playlist").stream()
      .map(f -> { Playlist p = playlistMapper.selectById(f.getResourceId()); if (p == null) return null; PlaylistVO vo = new PlaylistVO(); BeanUtils.copyProperties(p, vo); return vo; })
      .filter(v -> v != null).collect(Collectors.toList());
  }

  @Override
  public List<SingerVO> getFavoriteSingers(Long userId) {
    return userFavoriteMapper.selectByUserIdAndType(userId, "singer").stream()
      .map(f -> { Singer s = singerMapper.selectById(f.getResourceId()); if (s == null) return null; SingerVO vo = new SingerVO(); BeanUtils.copyProperties(s, vo); return vo; })
      .filter(v -> v != null).collect(Collectors.toList());
  }
}
