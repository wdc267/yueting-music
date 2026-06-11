-- 推荐表（recommend_type=banner 轮播图 / today 今日推荐）
CREATE TABLE `recommend` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title`         VARCHAR(200) DEFAULT NULL             COMMENT '标题',
    `cover`         VARCHAR(500) DEFAULT NULL             COMMENT '封面图（轮播图必传长方形，今日推荐可选正方形）',
    `resource_type` VARCHAR(20)  NOT NULL                COMMENT '资源类型：singer/song/playlist',
    `resource_id`   BIGINT       NOT NULL                COMMENT '资源ID',
    `recommend_type` VARCHAR(20) NOT NULL                COMMENT '推荐类型：banner/today',
    `status`        TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：0-禁用 1-启用',
    `sort_order`    INT          NOT NULL DEFAULT 0      COMMENT '排序',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_recommend_type` (`recommend_type`),
    KEY `idx_status` (`status`),
    KEY `idx_resource` (`resource_type`, `resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐表';