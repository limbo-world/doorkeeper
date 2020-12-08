/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table l_account
# ------------------------------------------------------------

DROP TABLE IF EXISTS `l_account`;

CREATE TABLE `l_account` (
  `account_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '账户ID',
  `username` varchar(32) NOT NULL COMMENT '账户名称',
  `password` varchar(128) NOT NULL DEFAULT '' COMMENT '密码',
  `nickname` varchar(32) NOT NULL DEFAULT '' COMMENT '昵称',
  `account_describe` varchar(32) NOT NULL COMMENT '账户描述',
  `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `uq_user` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `l_account` WRITE;
/*!40000 ALTER TABLE `l_account` DISABLE KEYS */;

INSERT INTO `l_account` (`account_id`, `username`, `password`, `nickname`, `account_describe`, `gmt_created`, `gmt_modified`)
VALUES
	(10000000,'admin','114674d5800fc1d905b93b4188dd7e029200404823b4ed83','admin','超级管理员xx','2020-11-24 14:47:46','2020-12-08 09:51:04');

/*!40000 ALTER TABLE `l_account` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table l_account_admin_role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `l_account_admin_role`;

CREATE TABLE `l_account_admin_role` (
  `account_admin_role_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) unsigned NOT NULL,
  `account_id` bigint(20) unsigned NOT NULL,
  `role_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`account_admin_role_id`),
  UNIQUE KEY `uq_p_a_r` (`project_id`,`account_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table l_account_role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `l_account_role`;

CREATE TABLE `l_account_role` (
  `account_role_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) unsigned NOT NULL,
  `account_id` bigint(20) unsigned NOT NULL,
  `role_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`account_role_id`),
  UNIQUE KEY `uq_account_role` (`account_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table l_permission
# ------------------------------------------------------------

DROP TABLE IF EXISTS `l_permission`;

CREATE TABLE `l_permission` (
  `permission_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) unsigned NOT NULL COMMENT '项目ID',
  `permission_name` varchar(32) NOT NULL DEFAULT '' COMMENT '权限名称',
  `http_method` varchar(32) NOT NULL,
  `url` varchar(128) NOT NULL,
  `permission_describe` varchar(64) NOT NULL DEFAULT '' COMMENT '权限描述',
  `is_online` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否上线',
  `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`permission_id`),
  UNIQUE KEY `uq_pro_per` (`project_id`,`permission_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `l_permission` WRITE;
/*!40000 ALTER TABLE `l_permission` DISABLE KEYS */;

INSERT INTO `l_permission` (`permission_id`, `project_id`, `permission_name`, `http_method`, `url`, `permission_describe`, `is_online`, `gmt_created`, `gmt_modified`)
VALUES
	(1002,10000000,'修改账户','PUT','/account/{\\d+}','',b'1','2020-12-01 22:36:43','2020-12-07 20:20:25'),
	(1003,10000000,'查询账户列表','GET','/account','',b'1','2020-12-01 22:37:12','2020-12-02 11:19:15'),
	(1005,10000000,'查询账户角色列表','GET','/account-role','',b'1','2020-12-01 22:38:18','2020-12-02 11:19:19'),
	(1007,10000000,'分页查询账户','GET','/account/query','',b'1','2020-12-01 22:37:33','2020-12-02 11:19:37'),
	(1009,10000000,'分页查询管理员','GET','/admin/query','',b'1','2020-12-01 22:41:13','2020-12-02 11:19:22'),
	(1011,10000000,'新增权限','POST','/permission','',b'1','2020-12-01 22:51:53','2020-12-02 11:21:22'),
	(1012,10000000,'批量修改权限','PUT','/permission','',b'1','2020-12-01 22:52:30','2020-12-02 11:21:24'),
	(1013,10000000,'查询权限列表','GET','/permission','',b'1','2020-12-01 22:53:01','2020-12-02 11:21:26'),
	(1014,10000000,'新增项目','POST','/project','',b'1','2020-12-01 22:53:37','2020-12-02 11:21:28'),
	(1015,10000000,'分页查询项目','GET','/project/query','',b'1','2020-12-01 22:54:42','2020-12-02 11:21:30'),
	(1016,10000000,'新增角色','POST','/role','',b'1','2020-12-01 22:55:48','2020-12-02 11:21:32'),
	(1017,10000000,'批量删除角色','DELETE','/role','',b'1','2020-12-01 22:56:33','2020-12-02 11:21:36'),
	(1018,10000000,'查询角色权限列表','GET','/role-permission','',b'1','2020-12-01 22:57:23','2020-12-02 11:21:38'),
	(1021,10000000,'更新管理员项目','PUT','/admin-project','',b'1','2020-12-01 22:42:27','2020-12-02 11:21:43'),
	(1024,10000000,'批量删除角色权限','DELETE','/role-permission','',b'1','2020-12-01 22:58:19','2020-12-02 11:21:47'),
	(1025,10000000,'批量添加角色权限','POST','/role-permission','',b'1','2020-12-01 22:58:02','2020-12-02 11:21:49'),
	(1026,10000000,'分页查询角色','GET','/role/query','',b'1','2020-12-01 22:56:59','2020-12-02 11:21:50'),
	(1027,10000000,'查询项目secret','GET','/project/{\\d+}/secret','',b'1','2020-12-01 22:55:20','2020-12-07 20:32:02'),
	(1028,10000000,'查询项目列表','GET','/project','',b'1','2020-12-01 22:54:18','2020-12-02 11:21:53'),
	(1029,10000000,'修改项目','PUT','/project/{\\d+}','',b'1','2020-12-01 22:54:01','2020-12-07 20:32:33'),
	(1030,10000000,'查询角色列表','GET','/role','',b'1','2020-12-01 22:56:46','2020-12-02 11:21:56'),
	(1031,10000000,'分页查询权限','GET','/permission/query','',b'1','2020-12-01 22:53:15','2020-12-02 11:21:57'),
	(1032,10000000,'批量删除权限','DELETE','/permission','',b'1','2020-12-01 22:52:41','2020-12-02 11:21:58'),
	(1033,10000000,'修改权限','PUT','/permission/{\\d+}','',b'1','2020-12-01 22:52:15','2020-12-07 20:28:07'),
	(1034,10000000,'修改角色','PUT','/role/{\\d+}','',b'1','2020-12-01 22:56:12','2020-12-07 20:33:56'),
	(1036,10000000,'新增账户','POST','/account','',b'1','2020-12-01 22:27:20','2020-12-02 11:22:05'),
	(1037,10000000,'新增管理员','POST','/admin','',b'1','2020-12-02 11:28:24','2020-12-02 11:28:24'),
	(1038,10000000,'修改管理员','PUT','/admin/{id:\\d+}','',b'1','2020-12-02 11:28:57','2020-12-02 11:28:57'),
	(1042,10000000,'获取管理端角色','GET','/account-admin-role','',b'1','2020-12-07 20:17:32','2020-12-07 20:17:32'),
	(1043,10000000,'批量绑定管理端角色','POST','/account-admin-role','',b'1','2020-12-07 20:17:51','2020-12-07 20:17:51'),
	(1044,10000000,'批量删除管理端角色','DELETE','/account-admin-role','',b'1','2020-12-07 20:18:05','2020-12-07 20:18:05'),
	(1045,10000000,'批量新增账户角色','POST','/account-role','',b'1','2020-12-07 20:24:38','2020-12-07 20:24:38'),
	(1046,10000000,'批量删除账户角色','DELETE','/account-role','',b'1','2020-12-07 20:24:55','2020-12-07 20:24:55'),
	(1048,10000000,'获取用户的授权信息，返回授予的角色、权限','GET','/auth/grant-info','',b'1','2020-12-07 20:26:05','2020-12-07 20:26:05'),
	(1049,10000000,'分页查询项目账户关系','GET','/project-account/query','',b'1','2020-12-07 20:29:07','2020-12-07 20:29:07'),
	(1050,10000000,'分页查询所有账户与项目关系','GET','/project-account/all-account','',b'1','2020-12-07 20:29:28','2020-12-07 20:29:28'),
	(1051,10000000,'添加项目账户','POST','/project-account','',b'1','2020-12-07 20:29:59','2020-12-07 20:29:59'),
	(1052,10000000,'修改项目账户','PUT','/project-account/{\\d+}','',b'1','2020-12-07 20:30:29','2020-12-07 20:30:29'),
	(1053,10000000,'批量修改项目账户','PUT','/project-account','',b'1','2020-12-07 20:30:45','2020-12-07 20:30:45'),
	(1054,10000000,'获取指定项目','GET','/project/{\\d+}','',b'1','2020-12-07 20:33:02','2020-12-07 20:33:02'),
	(1055,10000000,'查询所有管理端角色','GET','/role/admin','',b'1','2020-12-07 20:35:02','2020-12-07 20:35:02');

/*!40000 ALTER TABLE `l_permission` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table l_project
# ------------------------------------------------------------

DROP TABLE IF EXISTS `l_project`;

CREATE TABLE `l_project` (
  `project_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_name` varchar(64) NOT NULL COMMENT '项目名称',
  `project_secret` varchar(128) NOT NULL DEFAULT '' COMMENT '项目secret',
  `project_describe` varchar(128) NOT NULL DEFAULT '' COMMENT '项目备注、描述',
  `is_admin_project` bit(1) NOT NULL DEFAULT b'0',
  `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`project_id`),
  UNIQUE KEY `uq_name` (`project_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `l_project` WRITE;
/*!40000 ALTER TABLE `l_project` DISABLE KEYS */;

INSERT INTO `l_project` (`project_id`, `project_name`, `project_secret`, `project_describe`, `is_admin_project`, `gmt_created`, `gmt_modified`)
VALUES
	(10000000,'doorkeeper管理端','BEC00CF81411430FAC6EBDE343AADA86','doorkeeper管理端',b'1','2020-11-24 14:44:12','2020-11-26 19:06:49');

/*!40000 ALTER TABLE `l_project` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table l_project_account
# ------------------------------------------------------------

DROP TABLE IF EXISTS `l_project_account`;

CREATE TABLE `l_project_account` (
  `project_account_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  `is_admin` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否管理员',
  `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`project_account_id`),
  UNIQUE KEY `uq_account_project` (`account_id`,`project_id`) USING BTREE,
  UNIQUE KEY `uq_project_account` (`project_id`,`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `l_project_account` WRITE;
/*!40000 ALTER TABLE `l_project_account` DISABLE KEYS */;

INSERT INTO `l_project_account` (`project_account_id`, `account_id`, `project_id`, `is_admin`, `gmt_created`, `gmt_modified`)
VALUES
	(4,10000000,10000000,b'1','2020-12-03 17:03:36','2020-12-03 17:03:36');

/*!40000 ALTER TABLE `l_project_account` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table l_project_log
# ------------------------------------------------------------

DROP TABLE IF EXISTS `l_project_log`;

CREATE TABLE `l_project_log` (
  `project_log_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) unsigned NOT NULL COMMENT '项目ID',
  `account_id` bigint(20) unsigned NOT NULL COMMENT '账户ID',
  `business_type` varchar(32) NOT NULL DEFAULT '' COMMENT '业务类型',
  `operate_type` varchar(32) NOT NULL DEFAULT '' COMMENT '操作类型',
  `content` text NOT NULL COMMENT '操作内容',
  `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`project_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table l_role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `l_role`;

CREATE TABLE `l_role` (
  `role_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `project_id` bigint(20) unsigned NOT NULL COMMENT '项目ID',
  `role_name` varchar(64) NOT NULL COMMENT '角色名称',
  `role_describe` varchar(128) NOT NULL DEFAULT '' COMMENT '角色备注、描述',
  `is_default` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否默认角色，默认角色不能删除',
  `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `uq_pro_role` (`project_id`,`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `l_role` WRITE;
/*!40000 ALTER TABLE `l_role` DISABLE KEYS */;

INSERT INTO `l_role` (`role_id`, `project_id`, `role_name`, `role_describe`, `is_default`, `gmt_created`, `gmt_modified`)
VALUES
	(1000026,10000000,'权限查看','',b'0','2020-12-07 20:42:28','2020-12-07 20:44:21'),
	(1000027,10000000,'权限编辑','',b'0','2020-12-07 20:43:35','2020-12-07 20:43:35'),
	(1000028,10000000,'角色查看','',b'0','2020-12-07 20:44:05','2020-12-07 20:44:25'),
	(1000029,10000000,'角色编辑','',b'0','2020-12-07 20:45:23','2020-12-07 20:45:23'),
	(1000030,10000000,'账户查看','',b'0','2020-12-08 09:39:24','2020-12-08 09:39:24'),
	(1000031,10000000,'账户编辑','',b'0','2020-12-08 09:41:01','2020-12-08 09:41:01'),
	(1000032,10000000,'项目查看','',b'0','2020-12-08 09:42:54','2020-12-08 09:42:54'),
	(1000033,10000000,'项目秘钥','',b'0','2020-12-08 09:43:46','2020-12-08 09:43:46'),
	(1000034,10000000,'项目账户查看','',b'0','2020-12-08 09:45:34','2020-12-08 09:45:34'),
	(1000035,10000000,'项目账户编辑','',b'0','2020-12-08 09:47:23','2020-12-08 09:47:23'),
	(1000037,10000000,'项目编辑','',b'0','2020-12-08 09:54:51','2020-12-08 09:54:51');

/*!40000 ALTER TABLE `l_role` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table l_role_permission
# ------------------------------------------------------------

DROP TABLE IF EXISTS `l_role_permission`;

CREATE TABLE `l_role_permission` (
  `role_permission_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) unsigned NOT NULL,
  `role_id` bigint(20) unsigned NOT NULL,
  `permission_id` bigint(20) unsigned NOT NULL,
  `policy` varchar(16) NOT NULL DEFAULT '',
  PRIMARY KEY (`role_permission_id`),
  UNIQUE KEY `uq_role_per` (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `l_role_permission` WRITE;
/*!40000 ALTER TABLE `l_role_permission` DISABLE KEYS */;

INSERT INTO `l_role_permission` (`role_permission_id`, `project_id`, `role_id`, `permission_id`, `policy`)
VALUES
	(1092,10000000,1000026,1013,'allow'),
	(1093,10000000,1000026,1031,'allow'),
	(1094,10000000,1000027,1011,'allow'),
	(1095,10000000,1000027,1012,'allow'),
	(1096,10000000,1000027,1013,'allow'),
	(1097,10000000,1000027,1031,'allow'),
	(1098,10000000,1000027,1032,'allow'),
	(1099,10000000,1000027,1033,'allow'),
	(1100,10000000,1000028,1018,'allow'),
	(1101,10000000,1000028,1026,'allow'),
	(1102,10000000,1000028,1030,'allow'),
	(1103,10000000,1000028,1055,'allow'),
	(1104,10000000,1000029,1016,'allow'),
	(1105,10000000,1000029,1017,'allow'),
	(1106,10000000,1000029,1024,'allow'),
	(1107,10000000,1000029,1018,'allow'),
	(1108,10000000,1000029,1025,'allow'),
	(1109,10000000,1000029,1026,'allow'),
	(1110,10000000,1000029,1030,'allow'),
	(1111,10000000,1000029,1034,'allow'),
	(1112,10000000,1000029,1055,'allow'),
	(1113,10000000,1000030,1003,'allow'),
	(1114,10000000,1000030,1005,'allow'),
	(1115,10000000,1000030,1007,'allow'),
	(1116,10000000,1000030,1042,'allow'),
	(1117,10000000,1000031,1042,'allow'),
	(1119,10000000,1000031,1002,'allow'),
	(1120,10000000,1000031,1003,'allow'),
	(1121,10000000,1000031,1005,'allow'),
	(1122,10000000,1000031,1007,'allow'),
	(1123,10000000,1000031,1036,'allow'),
	(1124,10000000,1000031,1045,'allow'),
	(1125,10000000,1000031,1043,'allow'),
	(1126,10000000,1000031,1044,'allow'),
	(1127,10000000,1000031,1046,'allow'),
	(1128,10000000,1000032,1015,'allow'),
	(1129,10000000,1000032,1028,'allow'),
	(1130,10000000,1000032,1054,'allow'),
	(1131,10000000,1000033,1054,'allow'),
	(1132,10000000,1000033,1028,'allow'),
	(1133,10000000,1000033,1027,'allow'),
	(1134,10000000,1000033,1015,'allow'),
	(1135,10000000,1000034,1015,'allow'),
	(1136,10000000,1000034,1028,'allow'),
	(1137,10000000,1000034,1054,'allow'),
	(1138,10000000,1000034,1049,'allow'),
	(1139,10000000,1000034,1050,'allow'),
	(1140,10000000,1000035,1054,'allow'),
	(1141,10000000,1000035,1015,'allow'),
	(1142,10000000,1000035,1028,'allow'),
	(1143,10000000,1000035,1049,'allow'),
	(1144,10000000,1000035,1050,'allow'),
	(1145,10000000,1000035,1051,'allow'),
	(1146,10000000,1000035,1052,'allow'),
	(1147,10000000,1000035,1053,'allow'),
	(1160,10000000,1000035,1002,'allow'),
	(1169,10000000,1000037,1014,'allow'),
	(1170,10000000,1000037,1015,'allow'),
	(1171,10000000,1000037,1027,'allow'),
	(1172,10000000,1000037,1028,'allow'),
	(1173,10000000,1000037,1029,'allow'),
	(1174,10000000,1000037,1054,'allow');

/*!40000 ALTER TABLE `l_role_permission` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
