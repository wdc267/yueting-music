package com.yueting.web.controller;

import com.yueting.common.service.SingerService;
import com.yueting.common.service.SongService;
import com.yueting.common.service.PlaylistService;
import com.yueting.common.service.UserActionService;
import com.yueting.entity.vo.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/web/resource")
public class ResourceController {

  private final SingerService singerService;
  private final SongService songService;
  private final PlaylistService playlistService;
  private final UserActionService userActionService;

  public ResourceController(SingerService singerService, SongService songService,
                            PlaylistService playlistService, UserActionService userActionService) {
    this.singerService = singerService;
    this.songService = songService;
    this.playlistService = playlistService;
    this.userActionService = userActionService;
  }

  // 歌手详情（含歌曲列表）
  @GetMapping("/singer/{id}")
  public Result<Map<String, Object>> getSingerDetail(@PathVariable Long id) {
    SingerVO singer = singerService.getById(id);
    if (singer == null) return Result.error(404, "歌手不存在");
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("singer", singer);
    List<SongVO> songs = songService.selectEnabledList().stream()
      .filter(s -> Objects.equals(s.getSingerId(), id))
      .collect(java.util.stream.Collectors.toList());
    data.put("songs", songs);
    return Result.success(data);
  }

  // 歌单详情（含歌曲列表）
  @GetMapping("/playlist/{id}")
  public Result<PlaylistVO> getPlaylistDetail(@PathVariable Long id) {
    PlaylistVO vo = playlistService.getById(id);
    return vo != null ? Result.success(vo) : Result.error(404, "歌单不存在");
  }
}