/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50716
Source Host           : 127.0.0.1:3306
Source Database       : stockmonitor

Target Server Type    : MYSQL
Target Server Version : 50716
File Encoding         : 65001

Date: 2017-02-23 17:13:27
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for policy_step
-- ----------------------------
DROP TABLE IF EXISTS `policy_step`;
CREATE TABLE `policy_step` (
`id`  int(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
`user_id`  int(11) NOT NULL ,
`code`  char(6) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL ,
`policy_stat`  int(11) NOT NULL COMMENT '0-未开启\r\n1-待初始化\r\n2-区间执行\r\n3-已完成' ,
`price_last`  float NOT NULL ,
`price_init`  float NOT NULL ,
`count_init`  int(11) NOT NULL ,
`price_unit`  float NOT NULL ,
`step_unit`  int(10) UNSIGNED NOT NULL ,
`buy_offset`  float NOT NULL ,
`sell_offset`  float NOT NULL ,
`min_price`  float NOT NULL ,
`max_price`  float NOT NULL ,
`buyorder_id`  char(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL ,
`buylast_price`  float NULL DEFAULT NULL ,
`buyorder_date`  char(8) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL ,
`sellorder_id`  char(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL ,
`selllast_price`  float NULL DEFAULT NULL ,
`sellorder_date`  char(8) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=latin1 COLLATE=latin1_swedish_ci
AUTO_INCREMENT=9

;

-- ----------------------------
-- Table structure for trade_book
-- ----------------------------
DROP TABLE IF EXISTS `trade_book`;
CREATE TABLE `trade_book` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`user_id`  char(32) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL ,
`plat_id`  int(11) NOT NULL ,
`plat_order_id`  char(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL ,
`stat`  int(10) UNSIGNED NOT NULL ,
`code`  char(6) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL ,
`trade_flag`  int(10) UNSIGNED NOT NULL COMMENT '0-buy 1-sell' ,
`order_price`  float NOT NULL ,
`deal_price`  float NULL DEFAULT NULL ,
`count`  int(10) UNSIGNED NOT NULL ,
`counter_fee`  float UNSIGNED NOT NULL ,
`transfer_fee`  float UNSIGNED NOT NULL ,
`stamp_tax`  float UNSIGNED NOT NULL ,
`time`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=latin1 COLLATE=latin1_swedish_ci
AUTO_INCREMENT=31

;

-- ----------------------------
-- Table structure for userinfo
-- ----------------------------
DROP TABLE IF EXISTS `userinfo`;
CREATE TABLE `userinfo` (
`id`  int(11) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT ,
`plat_id`  int(10) UNSIGNED NOT NULL ,
`stat`  int(10) UNSIGNED NOT NULL DEFAULT 1 ,
`plat_acct`  tinytext CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL ,
`plat_psw`  tinytext CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=latin1 COLLATE=latin1_swedish_ci
AUTO_INCREMENT=3

;

-- ----------------------------
-- Auto increment value for policy_step
-- ----------------------------
ALTER TABLE `policy_step` AUTO_INCREMENT=9;

-- ----------------------------
-- Auto increment value for trade_book
-- ----------------------------
ALTER TABLE `trade_book` AUTO_INCREMENT=31;

-- ----------------------------
-- Auto increment value for userinfo
-- ----------------------------
ALTER TABLE `userinfo` AUTO_INCREMENT=3;
