package com.yueting.web.controller;

import com.yueting.common.service.UserActionService;
import com.yueting.entity.dto.UserFavoriteDTO;
import com.yueting.entity.dto.UserLikeDTO;
import com.yueting.entity.vo.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/web/user")
public class UserActionController {

  private final UserActionService userActionService;

  public UserActionController(UserActionService userActionService) {
    this.userActionService = userActionService;
  }

  @PostMapping("/like/toggle")
  public Result<Map<String, Object>> toggleLike(@RequestBody UserLikeDTO dto) {
    boolean liked = userActionService.toggleLike(dto);
    int count = userActionService.getLikeCount(dto.getResourceType(), dto.getResourceId());
    Map<String, Object> data = new HashMap<>();
    data.put("liked", liked);
    data.put("count", count);
    return Result.success(data);
  }

  @GetMapping("/like/status")
  public Result<Boolean> hasLiked(@RequestParam Long userId, @RequestParam String resourceType, @RequestParam Long resourceId) {
    return Result.success(userActionService.hasLiked(userId, resourceType, resourceId));
  }

  @PostMapping("/favorite/toggle")
  public Result<Map<String, Object>> toggleFavorite(@RequestBody UserFavoriteDTO dto) {
    boolean favorited = userActionService.toggleFavorite(dto);
    int count = userActionService.getFavoriteCount(dto.getResourceType(), dto.getResourceId());
    Map<String, Object> data = new HashMap<>();
    data.put("favorited", favorited);
    data.put("count", count);
    return Result.success(data);
  }

  @GetMapping("/favorite/status")
  public Result<Boolean> hasFavorited(@RequestParam Long userId, @RequestParam String resourceType, @RequestParam Long resourceId) {
    return Result.success(userActionService.hasFavorited(userId, resourceType, resourceId));
  }

  @PostMapping("/play/record")
  public Result<Void> recordPlay(@RequestParam Long songId, @RequestParam(required = false) Long userId) {
    userActionService.recordPlay(songId, userId);
    return Result.success();
  }

  @GetMapping("/liked/songs")
  public Result<List<SongVO>> getLikedSongs(@RequestParam Long userId) {
    return Result.success(userActionService.getLikedSongs(userId));
  }

  @GetMapping("/favorite/songs")
  public Result<List<SongVO>> getFavoriteSongs(@RequestParam Long userId) {
    return Result.success(userActionService.getFavoriteSongs(userId));
  }

  @GetMapping("/favorite/playlists")
  public Result<List<PlaylistVO>> getFavoritePlaylists(@RequestParam Long userId) {
    return Result.success(userActionService.getFavoritePlaylists(userId));
  }

  @GetMapping("/favorite/singers")
  public Result<List<SingerVO>> getFavoriteSingers(@RequestParam Long userId) {
    return Result.success(userActionService.getFavoriteSingers(userId));
  }

  @GetMapping("/profile")
  public Result<UserProfileVO> getProfile(@RequestParam Long userId) {
    return Result.success(userActionService.getProfile(userId));
  }
}
