package com.yueting.web.controller;

import com.yueting.common.service.RecommendService;
import com.yueting.common.service.SongService;
import com.yueting.common.service.SingerService;
import com.yueting.common.service.PlaylistService;
import com.yueting.entity.dto.RecommendQueryDTO;
import com.yueting.entity.vo.RecommendVO;
import com.yueting.entity.vo.SongVO;
import com.yueting.entity.vo.SingerVO;
import com.yueting.entity.vo.PlaylistVO;
import com.yueting.entity.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/web/home")
public class RecommendController {

    private final RecommendService recommendService;
    private final SongService songService;
    private final SingerService singerService;
    private final PlaylistService playlistService;

    public RecommendController(RecommendService recommendService,
                               SongService songService,
                               SingerService singerService,
                               PlaylistService playlistService) {
        this.recommendService = recommendService;
        this.songService = songService;
        this.singerService = singerService;
        this.playlistService = playlistService;
    }

    @GetMapping("/data")
    public Result<Map<String, Object>> getHomeData() {
        Map<String, Object> data = new LinkedHashMap<>();

        // 1. 轮播图
        RecommendQueryDTO bannerQuery = new RecommendQueryDTO();
        bannerQuery.setRecommendType("banner");
        bannerQuery.setStatus(1);
        data.put("banners", recommendService.selectList(bannerQuery));

        // 2. 今日推荐
        RecommendQueryDTO todayQuery = new RecommendQueryDTO();
        todayQuery.setRecommendType("today");
        todayQuery.setStatus(1);
        data.put("todayRecommends", recommendService.selectList(todayQuery));

        // 3. 推荐歌单
        data.put("playlists", playlistService.selectEnabledList());

        // 4. 热门歌手
        data.put("singers", singerService.selectEnabledList());

        // 5. 热门歌曲
        data.put("songs", songService.selectEnabledList());

        return Result.success(data);
    }
}