-- 项目
INSERT INTO `l_project`(`project_id`, `project_code`, `project_secret`, `project_name`, `project_desc`, `is_deleted`, `is_activated`) VALUES (10000000, 'limbo-authc', 'BEC00CF81411430FAC6EBDE343AADA86', '权限管理后台', '权限管理后台', b'0', b'1');

-- 权限;
INSERT INTO `l_permission`(`project_id`, `perm_code`, `perm_name`, `perm_desc`, `apis`, `is_online`, `is_default`) VALUES (10000000, 'authc.project.edit', '项目编辑', '项目的新增、修改、删除权限', 'POST /project,PUT /project/?*,DELETE /project/?*', b'1', b'1');
INSERT INTO `l_permission`(`project_id`, `perm_code`, `perm_name`, `perm_desc`, `apis`, `is_online`, `is_default`) VALUES (10000000, 'authc.project.query', '项目查询', '项目的批量查询、单个查询。', 'GET /project,GET /project/?*,GET /project/query', b'1', b'1');
INSERT INTO `l_permission`(`project_id`, `perm_code`, `perm_name`, `perm_desc`, `apis`, `is_online`, `is_default`) VALUES (10000000, 'authc.project.secret', '项目Secret查询', '可以查看项目的Secret', 'GET /project/?*/secret', b'1', b'1');
INSERT INTO `l_permission`(`project_id`, `perm_code`, `perm_name`, `perm_desc`, `apis`, `is_online`, `is_default`) VALUES (10000000, 'authc.admin.edit', '管理员编辑', '管理员的新增、修改(基础信息与密码)、删除。管理员是用于登录权限管理项目(Admin项目)的账号，其他接入认证授权管理的项目应该自行通过dubbo服务创建账户。', 'POST /admin,PUT /admin/*,DELETE /admin/*,PUT /admin/*/password', b'1', b'1');
INSERT INTO `l_permission`(`project_id`, `perm_code`, `perm_name`, `perm_desc`, `apis`, `is_online`, `is_default`) VALUES (10000000, 'authc.admin.project', '管理员项目编辑', '是否能够修改管理员关联的项目。', 'GET /admin-project,PUT /admin-project/*', b'1', b'1');
INSERT INTO `l_permission`(`project_id`, `perm_code`, `perm_name`, `perm_desc`, `apis`, `is_online`, `is_default`) VALUES (10000000, 'authc.account.grant', '账户授权', '修改账户授权的角色、权限策略权限。', 'PUT /account/*/grant', b'1', b'1');
INSERT INTO `l_permission`(`project_id`, `perm_code`, `perm_name`, `perm_desc`, `apis`, `is_online`, `is_default`) VALUES (10000000, 'authc.account.query', '账户查询', '账户的批量查询、单个查询。根据当前选中的项目查询所属项目的账户。', 'GET /account/**', b'1', b'1');
INSERT INTO `l_permission`(`project_id`, `perm_code`, `perm_name`, `perm_desc`, `apis`, `is_online`, `is_default`) VALUES (10000000, 'authc.session', '会话读取', '会话操作，读取会话信息、获取账号可用菜单、获取账户的项目、切换项目、注销登录等，全部账号都应拥有该权限', 'GET /admin-project/?*,GET /session,GET /session/menus,PUT /project/?*', b'1', b'1');
INSERT INTO `l_permission`(`project_id`, `perm_code`, `perm_name`, `perm_desc`, `apis`, `is_online`, `is_default`) VALUES (10000000, 'autho.perm.edit', '权限编辑', '权限的新增、修改、删除。', 'POST /permission,PUT /permission/?*,DELETE /permission/?*', b'1', b'1');
INSERT INTO `l_permission`(`project_id`, `perm_code`, `perm_name`, `perm_desc`, `apis`, `is_online`, `is_default`) VALUES (10000000, 'autho.perm.query', '权限查询', '权限的批量查询、单个查询。', 'GET /permission,GET /permission/query,GET /permission/?*', b'1', b'1');
INSERT INTO `l_permission`(`project_id`, `perm_code`, `perm_name`, `perm_desc`, `apis`, `is_online`, `is_default`) VALUES (10000000, 'autho.menu.edit', '菜单编辑', '菜单的新增、修改、删除。', 'POST /menu,PUT /menu/?*,DELETE /menu/?*', b'1', b'1');
INSERT INTO `l_permission`(`project_id`, `perm_code`, `perm_name`, `perm_desc`, `apis`, `is_online`, `is_default`) VALUES (10000000, 'autho.menu.query', '菜单查询', '菜单的批量查询、单个查询。', 'GET /menu,GET /menu/query,GET /menu/?*', b'1', b'1');
INSERT INTO `l_permission`(`project_id`, `perm_code`, `perm_name`, `perm_desc`, `apis`, `is_online`, `is_default`) VALUES (10000000, 'autho.role.edit', '角色编辑', '角色的新增、修改、删除。', 'POST /role,PUT /role/?*,DELETE /role/?*', b'1', b'1');
INSERT INTO `l_permission`(`project_id`, `perm_code`, `perm_name`, `perm_desc`, `apis`, `is_online`, `is_default`) VALUES (10000000, 'autho.role.grant', '角色授权', '将角色授权给用户。', 'PUT /role/?*/grant', b'1', b'1');
INSERT INTO `l_permission`(`project_id`, `perm_code`, `perm_name`, `perm_desc`, `apis`, `is_online`, `is_default`) VALUES (10000000, 'autho.role.query', '角色查询', '角色的批量查询、单个查询。', 'GET /role,GET /role/query,GET /role/?*', b'1', b'1');
INSERT INTO `l_permission`(`project_id`, `perm_code`, `perm_name`, `perm_desc`, `apis`, `is_online`, `is_default`) VALUES (10000000, 'blog.query', '日志查询', '查询操作日志', 'GET /b-log', b'1', b'1');

-- 菜单
INSERT INTO `l_menu`(`project_id`, `menu_code`, `menu_name`, `icon`, `sort`, `parent_menu_code`, `is_default`) VALUES (10000000, '001', '项目管理', 'el-icon-setting', 0, NULL, b'1');
INSERT INTO `l_menu`(`project_id`, `menu_code`, `menu_name`, `icon`, `sort`, `parent_menu_code`, `is_default`) VALUES (10000000, '001001', '项目列表', 'fa fa-project-diagram', 0, '001', b'1');
INSERT INTO `l_menu`(`project_id`, `menu_code`, `menu_name`, `icon`, `sort`, `parent_menu_code`, `is_default`) VALUES (10000000, '002', '账户管理', 'el-icon-lock', 1, NULL, b'1');
INSERT INTO `l_menu`(`project_id`, `menu_code`, `menu_name`, `icon`, `sort`, `parent_menu_code`, `is_default`) VALUES (10000000, '002001', '账户列表', 'el-icon-account', 0, '002', b'1');
INSERT INTO `l_menu`(`project_id`, `menu_code`, `menu_name`, `icon`, `sort`, `parent_menu_code`, `is_default`) VALUES (10000000, '003', '授权管理', 'el-icon-key', 2, NULL, b'1');
INSERT INTO `l_menu`(`project_id`, `menu_code`, `menu_name`, `icon`, `sort`, `parent_menu_code`, `is_default`) VALUES (10000000, '003001', '权限列表', 'el-icon-connection', 0, '003', b'1');
INSERT INTO `l_menu`(`project_id`, `menu_code`, `menu_name`, `icon`, `sort`, `parent_menu_code`, `is_default`) VALUES (10000000, '003002', '菜单列表', 'el-icon-menu', 1, '003', b'1');
INSERT INTO `l_menu`(`project_id`, `menu_code`, `menu_name`, `icon`, `sort`, `parent_menu_code`, `is_default`) VALUES (10000000, '003003', '角色列表', 'el-icon-postcard', 2, '003', b'1');
INSERT INTO `l_menu`(`project_id`, `menu_code`, `menu_name`, `icon`, `sort`, `parent_menu_code`, `is_default`) VALUES (10000000, '004', '日志', 'fa fa-file', 3, NULL, b'1');
INSERT INTO `l_menu`(`project_id`, `menu_code`, `menu_name`, `icon`, `sort`, `parent_menu_code`, `is_default`) VALUES (10000000, '004001', '操作日志', 'fa fa-file-alt', 0, '004', b'1');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '001001', 'authc.project.edit');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '001001', 'authc.project.query');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '001001', 'authc.project.secret');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '002001', 'authc.account.grant');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '002001', 'authc.account.query');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '002001', 'authc.admin.edit');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '002001', 'authc.admin.project');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '002001', 'autho.perm.query');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '002001', 'autho.role.query');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '003001', 'autho.perm.edit');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '003001', 'autho.perm.query');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '003002', 'autho.menu.edit');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '003002', 'autho.menu.query');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '003002', 'autho.perm.query');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '003003', 'authc.account.query');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '003003', 'autho.menu.query');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '003003', 'autho.perm.query');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '003003', 'autho.role.edit');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '003003', 'autho.role.grant');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '003003', 'autho.role.query');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '004001', 'authc.account.query');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '004001', 'authc.project.query');
INSERT INTO `l_menu_permission`(`project_id`, `menu_code`, `perm_code`) VALUES (10000000, '004001', 'blog.query');

-- 角色
INSERT INTO `l_role`(`project_id`, `role_id`, `role_name`, `role_desc`, `is_default`) VALUES (10000000, 10000000, '基础角色', '包含基础权限，会话读取等。', b'1');
INSERT INTO `l_role`(`project_id`, `role_id`, `role_name`, `role_desc`, `is_default`) VALUES (10000000, 10000001, '业务系统管理员', '业务系统管理员拥有授权相关菜单权限', b'1');
INSERT INTO `l_role_permission_policy`(`project_id`, `role_id`, `perm_code`, `policy`) VALUES (10000000, 10000000, 'authc.session', 'ALLOWED');
INSERT INTO `l_role_menu`(`project_id`, `role_id`, `menu_code`) VALUES (10000000, 10000001, '002');
INSERT INTO `l_role_menu`(`project_id`, `role_id`, `menu_code`) VALUES (10000000, 10000001, '002001');
INSERT INTO `l_role_menu`(`project_id`, `role_id`, `menu_code`) VALUES (10000000, 10000001, '003');
INSERT INTO `l_role_menu`(`project_id`, `role_id`, `menu_code`) VALUES (10000000, 10000001, '003001');
INSERT INTO `l_role_menu`(`project_id`, `role_id`, `menu_code`) VALUES (10000000, 10000001, '003002');
INSERT INTO `l_role_menu`(`project_id`, `role_id`, `menu_code`) VALUES (10000000, 10000001, '003003');

-- 账户，初始账户用户名：admin 密码：limbo-authc
INSERT INTO `l_account`(`account_id`, `project_id`, `username`, `password`, `nick`, `last_login`, `last_login_ip`, `is_super_admin`, `is_deleted`, `is_activated`) VALUES (10000000, 10000000, 'admin', 'f47459100b56901a0ab3726b65a865e99e3121749e85928e', '超级管理员', '2000-01-01 00:00:00', '', b'1', b'0', b'1');