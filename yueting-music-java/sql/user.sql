-- 用户信息表
CREATE TABLE `user_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `nickname` varchar(80) DEFAULT NULL COMMENT '昵称',
  `password` varchar(100) NOT NULL COMMENT '密码，加密后存储',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像地址',
  `gender` tinyint NOT NULL DEFAULT '0' COMMENT '性别：0-未知 1-男 2-女',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';

-- 用户点赞表：支持歌曲、歌单、歌手等资源
CREATE TABLE IF NOT EXISTS `user_like` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`       BIGINT      NOT NULL                COMMENT '用户ID，关联 user_info.id',
    `resource_type` VARCHAR(20) NOT NULL                COMMENT '资源类型：song-歌曲 playlist-歌单 singer-歌手',
    `resource_id`   BIGINT      NOT NULL                COMMENT '资源ID',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_resource_like` (`user_id`, `resource_type`, `resource_id`),
    KEY `idx_resource_like` (`resource_type`, `resource_id`),
    KEY `idx_user_like` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户点赞表';

-- 用户收藏表：支持歌曲、歌单、歌手等资源
CREATE TABLE IF NOT EXISTS `user_favorite` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`       BIGINT      NOT NULL                COMMENT '用户ID，关联 user_info.id',
    `resource_type` VARCHAR(20) NOT NULL                COMMENT '资源类型：song-歌曲 playlist-歌单 singer-歌手',
    `resource_id`   BIGINT      NOT NULL                COMMENT '资源ID',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_resource_favorite` (`user_id`, `resource_type`, `resource_id`),
    KEY `idx_resource_favorite` (`resource_type`, `resource_id`),
    KEY `idx_user_favorite` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 用户播放历史表：主要记录歌曲播放行为
CREATE TABLE IF NOT EXISTS `user_play_history` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`       BIGINT      NOT NULL                COMMENT '用户ID，关联 user_info.id',
    `song_id`       BIGINT      NOT NULL                COMMENT '歌曲ID，关联 song.id',
    `play_duration` INT         NOT NULL DEFAULT 0      COMMENT '本次播放时长，单位秒',
    `play_percent`  TINYINT     NOT NULL DEFAULT 0      COMMENT '播放进度百分比：0-100',
    `play_time`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '播放时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_play_time` (`user_id`, `play_time`),
    KEY `idx_song_play_time` (`song_id`, `play_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户播放历史表';
