/*
 Navicat Premium Data Transfer

 Source Server         : LimboDev
 Source Server Type    : MySQL
 Source Server Version : 50724
 Source Schema         : limbo_authc

 Target Server Type    : MySQL
 Target Server Version : 50724
 File Encoding         : 65001
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for l_account
-- ----------------------------
DROP TABLE IF EXISTS `l_account`;
CREATE TABLE `l_account` (
  `account_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '账户ID',
  `project_id` bigint(20) unsigned NOT NULL COMMENT '项目ID',
  `username` varchar(32) NOT NULL COMMENT '登录用户名',
  `password` varchar(256) NOT NULL COMMENT '加密后密码',
  `nick` varchar(32) NOT NULL COMMENT '昵称',
  `last_login` datetime NOT NULL DEFAULT '2000-01-01 00:00:00' COMMENT '上次登录时间',
  `last_login_ip` varchar(64) NOT NULL DEFAULT '' COMMENT '上次登录IP',
  `is_super_admin` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否为超级管理员',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `is_activated` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否激活',
  `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `unique_project_username` (`project_id`,`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10000000 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for l_account_permission_policy
-- ----------------------------
DROP TABLE IF EXISTS `l_account_permission_policy`;
CREATE TABLE `l_account_permission_policy` (
  `project_id` bigint(20) unsigned NOT NULL,
  `account_id` bigint(20) unsigned NOT NULL,
  `perm_code` varchar(32) NOT NULL,
  `policy` varchar(32) NOT NULL DEFAULT 'REFUSED',
  PRIMARY KEY (`project_id`,`account_id`,`perm_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for l_account_role
-- ----------------------------
DROP TABLE IF EXISTS `l_account_role`;
CREATE TABLE `l_account_role` (
  `project_id` bigint(20) unsigned NOT NULL,
  `account_id` bigint(20) unsigned NOT NULL,
  `role_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`project_id`,`account_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for l_menu
-- ----------------------------
DROP TABLE IF EXISTS `l_menu`;
CREATE TABLE `l_menu` (
  `project_id` bigint(20) unsigned NOT NULL COMMENT '项目ID',
  `menu_code` varchar(32) NOT NULL COMMENT '菜单code',
  `menu_name` varchar(32) NOT NULL COMMENT '菜单名',
  `menu_desc` varchar(512) NOT NULL DEFAULT '' COMMENT '菜单备注、描述',
  `icon` varchar(64) DEFAULT NULL COMMENT '菜单图标',
  `sort` int(8) NOT NULL DEFAULT 0 COMMENT '菜单排序',
  `parent_menu_code` varchar(32) DEFAULT NULL COMMENT '父菜单code',
  `is_default` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否默认菜单，默认菜单无法删除',
  `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `unique_project_menu_code` (`project_id`,`menu_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for l_menu_permission
-- ----------------------------
DROP TABLE IF EXISTS `l_menu_permission`;
CREATE TABLE `l_menu_permission` (
  `project_id` bigint(20) unsigned NOT NULL,
  `menu_code` varchar(32) NOT NULL,
  `perm_code` varchar(32) NOT NULL,
  PRIMARY KEY (`project_id`,`menu_code`,`perm_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for l_permission
-- ----------------------------
DROP TABLE IF EXISTS `l_permission`;
CREATE TABLE `l_permission` (
  `project_id` bigint(20) unsigned NOT NULL COMMENT '项目ID',
  `perm_code` varchar(32) NOT NULL COMMENT '权限code',
  `perm_name` varchar(32) NOT NULL COMMENT '权限名称',
  `perm_desc` varchar(512) DEFAULT NULL COMMENT '备注、描述',
  `apis` varchar(2048) DEFAULT NULL COMMENT '可访问API列表',
  `is_online` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否上线',
  `is_default` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否默认权限，无法删除',
  `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `unique_project_perm_code` (`project_id`,`perm_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for l_project
-- ----------------------------
DROP TABLE IF EXISTS `l_project`;
CREATE TABLE `l_project` (
  `project_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_code` varchar(32) NOT NULL COMMENT '项目code',
  `project_secret` varchar(32) NOT NULL COMMENT '项目secret',
  `project_name` varchar(64) NOT NULL COMMENT '项目名称',
  `project_desc` varchar(512) DEFAULT NULL COMMENT '项目备注、描述',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  `is_activated` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否激活',
  `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`project_id`),
  UNIQUE KEY `uq_code` (`project_code`) USING BTREE,
  UNIQUE KEY `uq_name` (`project_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10000000 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for l_role
-- ----------------------------
DROP TABLE IF EXISTS `l_role`;
CREATE TABLE `l_role` (
  `project_id` bigint(20) unsigned NOT NULL COMMENT '项目ID',
  `role_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(64) NOT NULL COMMENT '角色名称',
  `role_desc` varchar(128) DEFAULT NULL COMMENT '角色备注、描述',
  `is_default` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否默认角色，默认角色不能删除',
  `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000000 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for l_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `l_role_menu`;
CREATE TABLE `l_role_menu` (
  `project_id` bigint(20) unsigned NOT NULL,
  `role_id` bigint(20) unsigned NOT NULL,
  `menu_code` varchar(32) NOT NULL,
  PRIMARY KEY (`project_id`,`role_id`,`menu_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for l_role_permission_policy
-- ----------------------------
DROP TABLE IF EXISTS `l_role_permission_policy`;
CREATE TABLE `l_role_permission_policy` (
  `project_id` bigint(20) unsigned NOT NULL,
  `role_id` bigint(20) unsigned NOT NULL,
  `perm_code` varchar(32) NOT NULL,
  `policy` varchar(32) NOT NULL DEFAULT 'REFUSED',
  PRIMARY KEY (`project_id`,`role_id`,`perm_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
