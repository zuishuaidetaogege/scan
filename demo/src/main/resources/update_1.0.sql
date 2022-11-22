-- 2022-11-22 13:55:15 by MyName
ALTER TABLE `palletinfo` MODIFY COLUMN `palletid` varchar(255) COMMENT 'PALLETID' AFTER `create_time`;
-- 2022-11-22 13:55:15 by MyName
ALTER TABLE `palletinfo` MODIFY COLUMN `boxid` varchar(40) COMMENT 'BOXID' AFTER `palletid`;
-- 2022-11-22 13:55:15 by MyName
ALTER TABLE `palletinfo` MODIFY COLUMN `panelid` varchar(40) COMMENT 'PANELID' AFTER `boxid`;
-- 2022-11-22 13:55:15 by MyName
ALTER TABLE `palletinfo` MODIFY COLUMN `productid` varchar(40) COMMENT 'PRODUCTID' AFTER `panelid`;
-- 2022-11-22 13:55:15 by MyName
ALTER TABLE `palletinfo` MODIFY COLUMN `stepid` varchar(40) COMMENT 'STEPID' AFTER `productid`;
-- 2022-11-22 13:55:15 by MyName
ALTER TABLE `palletinfo` MODIFY COLUMN `prodtype` varchar(40) COMMENT 'PRODTYPE' AFTER `stepid`;
-- 2022-11-22 13:55:15 by MyName
ALTER TABLE `palletinfo` MODIFY COLUMN `status` varchar(40) COMMENT 'STATUS' AFTER `prodtype`;
-- 2022-11-22 13:55:15 by MyName
ALTER TABLE `palletinfo` MODIFY COLUMN `optioncode` varchar(255) COMMENT 'OPTIONCODE' AFTER `status`;
