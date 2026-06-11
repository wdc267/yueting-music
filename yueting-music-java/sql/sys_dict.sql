-- 创建字典表（自关联，parent_id=0 为字典类型，parent_id>0 为字典数据）
CREATE TABLE `sys_dict` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `parent_id`   BIGINT       NOT NULL DEFAULT 0     COMMENT '父级ID，0表示字典类型，>0表示字典数据',
    `dict_name`   VARCHAR(100) DEFAULT NULL            COMMENT '字典类型名称（类型节点使用）',
    `dict_code`   VARCHAR(50)  DEFAULT NULL            COMMENT '字典编码（类型节点使用，唯一）',
    `dict_label`  VARCHAR(100) DEFAULT NULL            COMMENT '字典数据标签（数据节点使用）',
    `dict_value`  VARCHAR(100) DEFAULT NULL            COMMENT '字典数据值（数据节点使用）',
    `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：0-禁用 1-启用',
    `remark`      VARCHAR(255) DEFAULT NULL            COMMENT '备注',
    `sort_order`  INT          NOT NULL DEFAULT 0      COMMENT '排序',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_code` (`dict_code`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据字典表';

-- 初始数据：音乐相关字典类型
INSERT INTO `sys_dict` (`parent_id`, `dict_name`, `dict_code`, `status`, `sort_order`, `remark`) VALUES
(0, '音乐类型',   'music_type',      1, 1, '如流行、古典、摇滚等'),
(0, '歌手地区',   'singer_region',   1, 2, '如华语、欧美、日韩等'),
(0, '音乐风格',   'music_style',     1, 3, '如轻快、伤感、激情等');

-- 音乐类型数据
INSERT INTO `sys_dict` (`parent_id`, `dict_label`, `dict_value`, `status`, `sort_order`) VALUES
(1, '流行',   'pop',      1, 1),
(1, '古典',   'classical',1, 2),
(1, '摇滚',   'rock',     1, 3),
(1, '民谣',   'folk',     1, 4),
(1, '电子',   'electronic',1, 5),
(1, '说唱',   'rap',      1, 6),
(1, '爵士',   'jazz',     1, 7);

-- 歌手地区数据
INSERT INTO `sys_dict` (`parent_id`, `dict_label`, `dict_value`, `status`, `sort_order`) VALUES
(2, '华语', 'chinese', 1, 1),
(2, '欧美', 'western', 1, 2),
(2, '日韩', 'japan_korea', 1, 3),
(2, '其他', 'other',   1, 4);

-- 音乐风格数据
INSERT INTO `sys_dict` (`parent_id`, `dict_label`, `dict_value`, `status`, `sort_order`) VALUES
(3, '轻快', 'upbeat',   1, 1),
(3, '伤感', 'sad',      1, 2),
(3, '激情', 'passionate',1, 3),
(3, '舒缓', 'soothing', 1, 4),
(3, '治愈', 'healing',  1, 5);