# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 10.219.192.172 (MySQL 5.7.32)
# Database: doorkeeper
# Generation Time: 2021-02-01 08:58:44 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table client
# ------------------------------------------------------------

DROP TABLE IF EXISTS `client`;

CREATE TABLE `client` (
  `client_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `realm_id` bigint(20) NOT NULL,
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT '名称',
  `description` varchar(256) NOT NULL DEFAULT '' COMMENT '描述',
  `is_enabled` bit(1) NOT NULL DEFAULT b'0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`client_id`),
  UNIQUE KEY `uq_client` (`realm_id`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table group_role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `group_role`;

CREATE TABLE `group_role` (
  `group_role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  `is_extend` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`group_role_id`),
  UNIQUE KEY `uq_key` (`group_id`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table group_user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `group_user`;

CREATE TABLE `group_user` (
  `group_user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `extend` varchar(1024) NOT NULL DEFAULT '' COMMENT '扩展信息',
  PRIMARY KEY (`group_user_id`),
  UNIQUE KEY `uq_key` (`group_id`,`user_id`),
  KEY `idx_user_group` (`user_id`,`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table groups
# ------------------------------------------------------------

DROP TABLE IF EXISTS `groups`;

CREATE TABLE `groups` (
  `group_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `realm_id` bigint(20) NOT NULL,
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT '名称',
  `description` varchar(256) NOT NULL DEFAULT '',
  `parent_id` bigint(20) NOT NULL,
  `is_default` bit(1) NOT NULL DEFAULT b'0' COMMENT '默认添加',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`group_id`),
  UNIQUE KEY `realm_id` (`realm_id`,`parent_id`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table permission
# ------------------------------------------------------------

DROP TABLE IF EXISTS `permission`;

CREATE TABLE `permission` (
  `permission_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `realm_id` bigint(20) NOT NULL,
  `client_id` bigint(20) NOT NULL,
  `name` varchar(256) NOT NULL DEFAULT '' COMMENT '名称',
  `description` varchar(256) NOT NULL DEFAULT '' COMMENT '描述',
  `logic` varchar(32) NOT NULL DEFAULT '' COMMENT '判断逻辑',
  `intention` varchar(32) NOT NULL DEFAULT '' COMMENT '执行逻辑',
  `is_enabled` bit(1) NOT NULL DEFAULT b'0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`permission_id`),
  UNIQUE KEY `uq_permission` (`realm_id`,`client_id`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table permission_policy
# ------------------------------------------------------------

DROP TABLE IF EXISTS `permission_policy`;

CREATE TABLE `permission_policy` (
  `permission_policy_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `permission_id` bigint(20) NOT NULL,
  `policy_id` bigint(20) NOT NULL,
  PRIMARY KEY (`permission_policy_id`),
  KEY `idx_permission` (`permission_id`,`policy_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table permission_resource
# ------------------------------------------------------------

DROP TABLE IF EXISTS `permission_resource`;

CREATE TABLE `permission_resource` (
  `permission_resource_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `permission_id` bigint(20) NOT NULL,
  `resource_id` bigint(20) NOT NULL,
  PRIMARY KEY (`permission_resource_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table policy
# ------------------------------------------------------------

DROP TABLE IF EXISTS `policy`;

CREATE TABLE `policy` (
  `policy_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `realm_id` bigint(20) NOT NULL,
  `client_id` bigint(20) NOT NULL,
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT '名称',
  `description` varchar(256) NOT NULL DEFAULT '' COMMENT '描述',
  `type` varchar(32) NOT NULL DEFAULT '' COMMENT '类型',
  `logic` varchar(32) NOT NULL DEFAULT '' COMMENT '判断逻辑',
  `intention` varchar(32) NOT NULL DEFAULT '' COMMENT '执行逻辑',
  `is_enabled` bit(1) NOT NULL DEFAULT b'0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`policy_id`),
  UNIQUE KEY `uq_policy` (`realm_id`,`client_id`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table policy_group
# ------------------------------------------------------------

DROP TABLE IF EXISTS `policy_group`;

CREATE TABLE `policy_group` (
  `policy_group_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `policy_id` bigint(20) NOT NULL,
  `group_id` bigint(20) NOT NULL,
  `is_extend` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`policy_group_id`),
  UNIQUE KEY `uq_group` (`policy_id`,`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table policy_param
# ------------------------------------------------------------

DROP TABLE IF EXISTS `policy_param`;

CREATE TABLE `policy_param` (
  `policy_param_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `policy_id` bigint(20) NOT NULL,
  `k` varchar(128) NOT NULL DEFAULT '',
  `v` varchar(128) NOT NULL DEFAULT '',
  `kv` varchar(256) NOT NULL DEFAULT '',
  PRIMARY KEY (`policy_param_id`),
  UNIQUE KEY `uq_k_v` (`policy_id`,`k`,`v`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table policy_role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `policy_role`;

CREATE TABLE `policy_role` (
  `policy_role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `policy_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`policy_role_id`),
  UNIQUE KEY `uq_role` (`policy_id`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table policy_user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `policy_user`;

CREATE TABLE `policy_user` (
  `policy_user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `policy_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`policy_user_id`),
  UNIQUE KEY `uq_user` (`policy_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table realm
# ------------------------------------------------------------

DROP TABLE IF EXISTS `realm`;

CREATE TABLE `realm` (
  `realm_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL DEFAULT '' COMMENT '名称',
  `secret` varchar(64) NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`realm_id`),
  UNIQUE KEY `uq_realm` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table resource
# ------------------------------------------------------------

DROP TABLE IF EXISTS `resource`;

CREATE TABLE `resource` (
  `resource_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `realm_id` bigint(20) NOT NULL,
  `client_id` bigint(20) NOT NULL,
  `name` varchar(256) NOT NULL DEFAULT '' COMMENT '名称',
  `description` varchar(256) NOT NULL DEFAULT '' COMMENT '描述',
  `is_enabled` bit(1) NOT NULL DEFAULT b'0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`resource_id`),
  UNIQUE KEY `uq_resource` (`realm_id`,`client_id`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table resource_association
# ------------------------------------------------------------

DROP TABLE IF EXISTS `resource_association`;

CREATE TABLE `resource_association` (
  `resource_association_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `resource_id` bigint(20) NOT NULL,
  `parent_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`resource_association_id`),
  UNIQUE KEY `uq_resource` (`resource_id`,`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;


# Dump of table resource_tag
# ------------------------------------------------------------

DROP TABLE IF EXISTS `resource_tag`;

CREATE TABLE `resource_tag` (
  `resource_tag_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `resource_id` bigint(20) NOT NULL,
  `tag_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`resource_tag_id`),
  UNIQUE KEY `uq_resource` (`resource_id`,`tag_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table resource_uri
# ------------------------------------------------------------

DROP TABLE IF EXISTS `resource_uri`;

CREATE TABLE `resource_uri` (
  `resource_uri_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `resource_id` bigint(20) NOT NULL,
  `uri_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`resource_uri_id`),
  UNIQUE KEY `uq_uri` (`resource_id`,`uri_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;


# Dump of table role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `realm_id` bigint(20) NOT NULL,
  `client_id` bigint(20) NOT NULL DEFAULT '0',
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT '名称',
  `description` varchar(256) NOT NULL DEFAULT '' COMMENT '描述',
  `is_enabled` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否启用',
  `is_combine` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否组合角色',
  `is_default` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否默认添加',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `uq_role` (`realm_id`,`client_id`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table role_combine
# ------------------------------------------------------------

DROP TABLE IF EXISTS `role_combine`;

CREATE TABLE `role_combine` (
  `role_combine_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`role_combine_id`),
  UNIQUE KEY `uq_key` (`parent_id`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table tag
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tag`;

CREATE TABLE `tag` (
  `tag_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `realm_id` bigint(20) NOT NULL,
  `client_id` bigint(20) NOT NULL,
  `k` varchar(128) NOT NULL DEFAULT '',
  `v` varchar(128) NOT NULL DEFAULT '',
  `kv` varchar(256) NOT NULL DEFAULT '',
  PRIMARY KEY (`tag_id`) USING BTREE,
  UNIQUE KEY `uq_tag` (`realm_id`,`client_id`,`k`,`v`) USING BTREE,
  KEY `idx_r_c_kv` (`realm_id`,`client_id`,`kv`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table uri
# ------------------------------------------------------------

DROP TABLE IF EXISTS `uri`;

CREATE TABLE `uri` (
  `uri_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `realm_id` bigint(20) NOT NULL,
  `client_id` bigint(20) NOT NULL,
  `uri` varchar(256) NOT NULL DEFAULT '' COMMENT 'ant风格uri',
  `method` varchar(16) NOT NULL DEFAULT '' COMMENT 'http请求方法',
  PRIMARY KEY (`uri_id`) USING BTREE,
  UNIQUE KEY `uq_uri` (`realm_id`,`client_id`,`uri`,`method`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `realm_id` bigint(20) NOT NULL,
  `username` varchar(128) NOT NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(128) NOT NULL DEFAULT '' COMMENT '密码',
  `nickname` varchar(128) NOT NULL COMMENT '昵称',
  `description` varchar(256) NOT NULL DEFAULT '',
  `email` varchar(128) NOT NULL DEFAULT '',
  `phone` varchar(16) NOT NULL DEFAULT '',
  `extend` varchar(1024) NOT NULL DEFAULT '',
  `is_enabled` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uq_user` (`realm_id`,`username`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;



# Dump of table user_role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user_role`;

CREATE TABLE `user_role` (
  `user_role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_role_id`),
  UNIQUE KEY `uq_key` (`user_id`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
