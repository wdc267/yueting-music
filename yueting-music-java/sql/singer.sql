-- 歌手信息表
CREATE TABLE `singer` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`        VARCHAR(100) NOT NULL                COMMENT '歌手姓名',
    `avatar`      VARCHAR(500) DEFAULT NULL             COMMENT '头像URL',
    `region`      VARCHAR(50)  DEFAULT NULL             COMMENT '地区编码，关联字典 singer_region',
    `status`      TINYINT      NOT NULL DEFAULT 1       COMMENT '状态：0-下架 1-上架',
    `remark`      VARCHAR(255) DEFAULT NULL             COMMENT '备注',
    `sort_order`  INT          NOT NULL DEFAULT 0       COMMENT '排序，数字越小越靠前',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_region` (`region`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='歌手信息表';