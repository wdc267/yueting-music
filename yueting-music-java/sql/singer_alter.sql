-- singer 表增加 sort_order 字段（ALTER 脚本，已有表则执行）
ALTER TABLE `singer`
    ADD COLUMN `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序，数字越小越靠前' AFTER `remark`;