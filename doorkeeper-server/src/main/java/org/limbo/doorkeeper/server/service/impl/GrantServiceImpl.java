/*
 *  Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.limbo.doorkeeper.server.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.authc.api.interfaces.beans.po.*;
import org.limbo.authc.api.interfaces.beans.vo.AuthorizationVO;
import org.limbo.authc.api.interfaces.beans.vo.MenuVO;
import org.limbo.authc.api.interfaces.beans.vo.PermissionVO;
import org.limbo.authc.api.interfaces.constants.PermissionAuthcPolicies;
import org.limbo.authc.api.interfaces.utils.EnhancedBeanUtils;
import org.limbo.authc.api.interfaces.utils.EnhancedCollectionUtils;
import org.limbo.authc.api.interfaces.utils.verify.Verifies;
import org.limbo.authc.core.dao.*;
import org.limbo.authc.core.service.GrantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Brozen
 * @date 2020/3/4 12:34 PM
 * @email brozen@qq.com
 */
@Service
public class GrantServiceImpl implements GrantService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private GrantMapper grantMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private MenuPermissionMapper menuPermissionMapper;

    @Autowired
    private RolePermissionPolicyMapper rolePermissionPolicyMapper;

    @Autowired
    private AccountPermissionPolicyMapper accountPermissionPolicyMapper;

    @Autowired
    private PermissionMapper permissionMapper;


    @Override
    @Transactional
    public Integer grantRole(AuthorizationVO.GrantParam param) {
        List<Long> roleIds = param.getRoleIds();
        List<Long> accountIds = param.getAccountIds();
        AtomicInteger influenced = new AtomicInteger(0);

        roleIds.forEach(roleId -> {
            grantMapper.revokeFromAccountNotIn(param.getProjectId(), roleId, accountIds);
            influenced.addAndGet(grantMapper.grantRoleToAccounts(param.getProjectId(), roleId, accountIds));
        });
        return influenced.get();
    }

    @Override
    @Transactional
    public Integer revokeRole(AuthorizationVO.GrantParam param) {
        List<Long> roleIds = param.getRoleIds();
        List<Long> accountIds = param.getAccountIds();
        AtomicInteger influenced = new AtomicInteger(0);

        roleIds.forEach(roleId ->
                influenced.addAndGet(grantMapper.revokeFromAccountIn(param.getProjectId(), roleId, accountIds))
        );
        return influenced.get();
    }

    @Override
    @Transactional
    public Integer updateGrant(AuthorizationVO.RoleGrantParam param) {
        Long roleId = param.getRoleId();
        List<Long> accountIds = param.getAccountIds();

        Long projectId = param.getProjectId();
        grantMapper.revoke(projectId, roleId);
        return grantMapper.grantRoleToAccounts(projectId, roleId, accountIds);
    }

    @Override
    @Transactional
    public Integer updateGrant(AuthorizationVO.AccountGrantParam param) {
        Long accountId = param.getAccountId();
        List<Long> roleIds = param.getRoleIds();

        // 更新账户权限策略
        accountPermissionPolicyMapper.deletePolicies(param.getProjectId(), param.getAccountId());
        if (CollectionUtils.isNotEmpty(param.getPermPolicies())) {
            List<AccountPermissionPolicyPO> policies = param.getPermPolicies().stream().map(p -> {
                AccountPermissionPolicyPO policy = new AccountPermissionPolicyPO();
                policy.setProjectId(param.getProjectId());
                policy.setAccountId(param.getAccountId());
                policy.setPermCode(p.getPermCode());
                policy.setPolicy(p.getPolicy().getValue());
                return policy;
            }).collect(Collectors.toList());
            accountPermissionPolicyMapper.addPolicies(policies);
        }

        Integer influenced = 0;
        // 更新账户角色授权
        grantMapper.revokeFromAccounts(param.getProjectId(), Collections.singletonList(accountId));
        if (CollectionUtils.isNotEmpty(roleIds)) {
            influenced = grantMapper.grantRolesToAccount(param.getProjectId(), roleIds, accountId);
        }
        return influenced;
    }

    @Override
    public List<MenuVO> getAccountMenus(Long projectId, Long accountId) {
        AccountPO account = accountMapper.getAccount(projectId, accountId);
        Verifies.notNull(account, "账户不存在！");

        if (account != null && account.getIsSuperAdmin()) {
            // 超级管理员拥有全部菜单
            List<MenuPO> allMenus = menuMapper.getMenus(projectId);
            return EnhancedBeanUtils.createAndCopyList(allMenus, MenuVO.class);
        }

        // 查询账户关联角色拥有的菜单
        List<String> menuCodes = null;
        List<RolePO> roles = grantMapper.getGrantedRoles(projectId, Collections.singletonList(accountId));
        List<Long> roleIds = roles.stream()
                .map(RolePO::getRoleId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(roleIds)) {
            menuCodes = roleMenuMapper.getRoleMenuCodes(projectId, roleIds);
            menuCodes = menuCodes == null ? new LinkedList<>() : menuCodes;
        }

        // 去重
        Set<String> distinct = menuCodes == null ? Collections.emptySet() : new HashSet<>(menuCodes);
        if (CollectionUtils.isNotEmpty(distinct)) {
            List<MenuPO> menus = menuMapper.selectList(Wrappers.<MenuPO>lambdaQuery()
                    .eq(BasePO::getProjectId, projectId)
                    .in(MenuPO::getMenuCode, distinct));
            return EnhancedBeanUtils.createAndCopyList(menus, MenuVO.class);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<MenuVO> getAnonMenus(Long projectId) {
        // warn 这里的ANON角色是需要在DB初始化时插入进去的
        RolePO anonRole = roleMapper.getByRoleName(projectId, "ANON");
        if (anonRole == null) {
            return Collections.emptyList();
        }

        List<MenuPO> menus = roleMenuMapper.getRoleMenus(projectId, Collections.singletonList(anonRole.getRoleId()));
        return EnhancedBeanUtils.createAndCopyList(menus, MenuVO.class);
    }

    @Override
    public List<PermissionVO> getAccountPermissions(Long projectId, Long accountId) {
        AccountPO account = accountMapper.getAccount(projectId, accountId);
        Verifies.notNull(account, "账户不存在！");

        if (account.getIsSuperAdmin()) {
            // 超级管理员拥有全部权限
            List<PermissionPO> allPermissions = permissionMapper.getPermissions(projectId);
            return EnhancedBeanUtils.createAndCopyList(allPermissions, PermissionVO.class);
        }

        // 查询角色关联菜单的权限
        List<Long> roleIds = grantMapper.getGrantedRoleIds(projectId, Collections.singletonList(accountId));
        List<String> rolePermCodes = null;
        if (CollectionUtils.isNotEmpty(roleIds)) {
            List<String> roleMenuCodes = roleMenuMapper.getRoleMenuCodes(projectId, roleIds);
            rolePermCodes = menuPermissionMapper.getMenuPermissionCodes(projectId, roleMenuCodes);
            rolePermCodes = rolePermCodes == null ? new LinkedList<>() : rolePermCodes;
            // 添加角色通过策略允许的权限
            rolePermCodes.addAll(rolePermissionPolicyMapper.getAllowedPermCodes(projectId, roleIds));
            // 移除角色通过策略拒绝的权限
            rolePermCodes.removeAll(rolePermissionPolicyMapper.getRefusedPermCodes(projectId, roleIds));
        }

        // 添加账号通过策略通过的权限
        Set<String> distinctPermCodes = rolePermCodes == null ? new HashSet<>() : new HashSet<>(rolePermCodes);
        List<AccountPermissionPolicyPO> policies = accountPermissionPolicyMapper.getPolicies(projectId, Collections.singletonList(accountId), null);
        Map<String, List<AccountPermissionPolicyPO>> groupedPolices = policies.stream()
                .collect(Collectors.groupingBy(AccountPermissionPolicyPO::getPolicy, Collectors.toList()));
        List<String> allowedPermCode = Optional.ofNullable(groupedPolices.get(PermissionAuthcPolicies.ALLOWED.name()))
                .orElse(Collections.emptyList()).stream()
                .map(AccountPermissionPolicyPO::getPermCode).collect(Collectors.toList());
        distinctPermCodes.addAll(allowedPermCode);

        // 移除账号通过策略拒绝的权限
        Set<String> refusedPermCode = Optional.ofNullable(groupedPolices.get(PermissionAuthcPolicies.REFUSED.name()))
                .orElse(Collections.emptyList()).stream()
                .map(AccountPermissionPolicyPO::getPermCode).collect(Collectors.toSet());
        distinctPermCodes.removeAll(refusedPermCode);

        // 去重
        List<PermissionPO> permissions = permissionMapper.selectList(Wrappers.<PermissionPO>lambdaQuery()
                .eq(BasePO::getProjectId, projectId)
                .in(PermissionPO::getPermCode, distinctPermCodes));
        return EnhancedBeanUtils.createAndCopyList(permissions, PermissionVO.class);
    }

    @Override
    public List<PermissionVO> getAnonPermissions(Long projectId) {
        // warn 这里的ANON角色是需要在DB初始化时插入进去的
        RolePO anonRole = roleMapper.getByRoleName(projectId, "ANON");
        if (anonRole == null) {
            return Collections.emptyList();
        }

        // 查询匿名角色关联菜单的权限
        List<Long> roleIds = Collections.singletonList(anonRole.getRoleId());
        List<String> roleMenuCodes = roleMenuMapper.getRoleMenuCodes(projectId, roleIds);
        List<String> rolePermCodes = menuPermissionMapper.getMenuPermissionCodes(projectId, roleMenuCodes);
        // 添加角色通过策略允许的权限
        rolePermCodes.addAll(rolePermissionPolicyMapper.getAllowedPermCodes(projectId, roleIds));
        // 移除角色通过策略拒绝的权限
        rolePermCodes.removeAll(rolePermissionPolicyMapper.getRefusedPermCodes(projectId, roleIds));
        // 去重
        rolePermCodes = EnhancedCollectionUtils.distinct(rolePermCodes, p -> p, ArrayList::new);
        List<PermissionPO> permissions = permissionMapper.selectList(Wrappers.<PermissionPO>lambdaQuery()
                .eq(BasePO::getProjectId, projectId)
                .in(PermissionPO::getPermCode, rolePermCodes));
        return EnhancedBeanUtils.createAndCopyList(permissions, PermissionVO.class);
    }

}
