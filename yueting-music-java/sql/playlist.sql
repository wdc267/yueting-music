-- 歌单表
CREATE TABLE `playlist` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`        VARCHAR(200) NOT NULL                COMMENT '歌单名称',
    `cover`       VARCHAR(500) DEFAULT NULL             COMMENT '封面图URL',
    `description` TEXT         DEFAULT NULL             COMMENT '简介',
    `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：0-下架 1-上架',
    `sort_order`  INT          NOT NULL DEFAULT 0      COMMENT '排序',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='歌单表';

-- 歌单歌曲关联表
CREATE TABLE `playlist_song` (
    `id`          BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `playlist_id` BIGINT  NOT NULL                COMMENT '歌单ID',
    `song_id`     BIGINT  NOT NULL                COMMENT '歌曲ID',
    `sort_order`  INT     NOT NULL DEFAULT 0      COMMENT '歌曲在歌单内的排序',
    PRIMARY KEY (`id`),
    KEY `idx_playlist_id` (`playlist_id`),
    KEY `idx_song_id` (`song_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='歌单歌曲关联表';