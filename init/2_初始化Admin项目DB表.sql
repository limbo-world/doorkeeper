/*
 Navicat Premium Data Transfer

 Source Server         : LimboDev
 Source Server Type    : MySQL
 Source Server Version : 50724
 Source Schema         : limbo_auth_admin

 Target Server Type    : MySQL
 Target Server Version : 50724
 File Encoding         : 65001
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for laa_account
-- ----------------------------
DROP TABLE IF EXISTS `laa_account`;
CREATE TABLE `laa_account` (
  `account_id` bigint(20) NOT NULL COMMENT '账户ID',
  `project_id` bigint(20) NOT NULL,
  `is_super_admin` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否为超级管理员',
  `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for laa_account_project
-- ----------------------------
DROP TABLE IF EXISTS `laa_account_project`;
CREATE TABLE `laa_account_project` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `account_id` bigint(11) NOT NULL,
  `project_id` bigint(11) NOT NULL,
  `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_account_project` (`account_id`,`project_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for laa_project
-- ----------------------------
DROP TABLE IF EXISTS `laa_project`;
CREATE TABLE `laa_project` (
  `project_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_name` varchar(64) NOT NULL COMMENT '项目名称',
  `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`project_id`),
  UNIQUE KEY `uq_name` (`project_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10000000 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for laa_business_log
-- ----------------------------
DROP TABLE IF EXISTS `laa_business_log`;
CREATE TABLE `laa_business_log` (
  `log_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) unsigned DEFAULT NULL,
  `project_name` varchar(64) DEFAULT NULL,
  `account_id` bigint(20) unsigned NOT NULL,
  `account_nick` varchar(64) NOT NULL,
  `log_name` varchar(256) NOT NULL,
  `log_type` varchar(32) NOT NULL,
  `ip` varchar(128) NOT NULL,
  `param` varchar(2048) DEFAULT NULL,
  `session` varchar(2048) DEFAULT NULL,
  `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100000000 DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;