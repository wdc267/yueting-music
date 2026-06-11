-- 歌曲表增加播放数、点赞数、收藏数
ALTER TABLE `song`
    ADD COLUMN `play_count`  INT NOT NULL DEFAULT 0 COMMENT '播放数' AFTER `lyric`,
    ADD COLUMN `like_count`  INT NOT NULL DEFAULT 0 COMMENT '点赞数' AFTER `play_count`,
    ADD COLUMN `fav_count`   INT NOT NULL DEFAULT 0 COMMENT '收藏数' AFTER `like_count`;