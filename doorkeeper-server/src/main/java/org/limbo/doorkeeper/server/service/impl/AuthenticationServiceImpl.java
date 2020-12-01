/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.limbo.doorkeeper.server.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.constants.PermissionPolicy;
import org.limbo.doorkeeper.api.model.param.AuthenticationCheckParam;
import org.limbo.doorkeeper.api.model.vo.AccountPermissionGrantVO;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.limbo.doorkeeper.server.dao.*;
import org.limbo.doorkeeper.server.entity.*;
import org.limbo.doorkeeper.server.service.AuthenticationService;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author liuqingtong
 * @date 2020/11/25 18:09
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final ThreadLocal<AntPathMatcher> PATH_MATCHER = ThreadLocal.withInitial(AntPathMatcher::new);

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private AuthenticationService _this;

    @Autowired
    private AccountRoleMapper accountRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public Boolean accessAllowed(Long projectId, AuthenticationCheckParam param) {
        Account account = accountMapper.getProjcetAccountById(projectId, param.getAccountId());
        if (account == null) {
            return false;
        }

        // 管理员拥有全部权限
        if (account.getIsAdmin()) {
            return true;
        }

        // 拿到用户全部权限
        AccountPermissionGrantVO grants = _this.getGrantedApis(projectId, account.getAccountId());
        // 如果没有授权信息 或 授权访问的API列表为空，则禁止访问
        if (grants == null || CollectionUtils.isEmpty(grants.getAllowedPermissions())) {
            return false;
        }

        // 首先检测refused
        List<PermissionVO> refusedPermissions = grants.getRefusedPermissions();
        if (CollectionUtils.isNotEmpty(refusedPermissions)) {
            // 如果存在请求方法与请求路径匹配，说明禁止访问
            if (refusedPermissions.stream().anyMatch(permission -> permissionMatch(permission, param))) {
                return false;
            }
        }

        // 然后检测allowed
        List<PermissionVO> allowedPermissions = grants.getAllowedPermissions();
        // 如果存在请求方法与请求路径匹配，说明允许访问
        return allowedPermissions.stream().anyMatch(permission -> permissionMatch(permission, param));
    }

    /**
     * 检测配置与请求检查参数的请求url是否匹配，检测method和path两部分
     */
    private boolean permissionMatch(PermissionVO permission, AuthenticationCheckParam param) {
        AntPathMatcher antPathMatcher = PATH_MATCHER.get();
        return methodMatch(permission.getHttpMethod(), param.getMethod())
                && antPathMatcher.match(permission.getUrl(), param.getPath());
    }

    /**
     * 判断请求方法是否匹配，如果未设定要求的请求方法，则所有请求方法都匹配
     *
     * @param requireMethod 要求的请求方法
     * @param requestMethod 实际请求的方法
     */
    private boolean methodMatch(String requireMethod, String requestMethod) {
        return StringUtils.isBlank(requireMethod) || requireMethod.equalsIgnoreCase(requestMethod);
    }

    /**
     * {@inheritDoc}
     * 考虑添加缓存
     *
     * @param projectId
     * @param accountId
     * @return
     */
    @Override
    public List<RoleVO> getGrantedRoles(Long projectId, Long accountId) {
        List<AccountRole> accountRoles = accountRoleMapper.selectList(
                Wrappers.<AccountRole>lambdaQuery()
                        .eq(AccountRole::getProjectId, projectId)
                        .eq(AccountRole::getAccountId, accountId));
        Set<Long> roleIds = accountRoles.stream()
                .map(AccountRole::getRoleId)
                .collect(Collectors.toSet());

        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        return EnhancedBeanUtils.createAndCopyList(roles, RoleVO.class);
    }

    /**
     * {@inheritDoc}
     * 考虑添加缓存
     *
     * @param projectId
     * @param accountId
     * @return
     */
    @Override
    public List<PermissionVO> getGrantedPermissions(Long projectId, Long accountId) {
        // 用户授权的角色
        List<RoleVO> roles = _this.getGrantedRoles(projectId, accountId);
        if (CollectionUtils.isEmpty(roles)) {
            return new ArrayList<>();
        }

        Set<Long> roleIds = roles.stream().map(RoleVO::getRoleId).collect(Collectors.toSet());

        // 角色对应的权限
        List<RolePermission> rolePerms = rolePermissionMapper.selectList(Wrappers.<RolePermission>lambdaQuery()
                .eq(RolePermission::getProjectId, projectId)
                .in(RolePermission::getRoleId, roleIds)
        );
        if (CollectionUtils.isEmpty(rolePerms)) {
            return new ArrayList<>();
        }

        Set<Long> permIds = rolePerms.stream().map(RolePermission::getPermissionId).collect(Collectors.toSet());

        List<Permission> perms = permissionMapper.selectBatchIds(permIds);
        return EnhancedBeanUtils.createAndCopyList(perms, PermissionVO.class);
    }

    /**
     * {@inheritDoc}
     * 考虑添加缓存 @Cache
     *
     * @param projectId
     * @param accountId
     * @return
     */
    @Override
    public AccountPermissionGrantVO getGrantedApis(Long projectId, Long accountId) {
        AccountPermissionGrantVO accountPermissionGrant = new AccountPermissionGrantVO();
        accountPermissionGrant.setAccountId(accountId);
        accountPermissionGrant.setAllowedPermissions(new ArrayList<>());
        accountPermissionGrant.setRefusedPermissions(new ArrayList<>());

        // 查询用户授权的所有权限
        List<RoleVO> roles = _this.getGrantedRoles(projectId, accountId);
        if (CollectionUtils.isEmpty(roles)) {
            return accountPermissionGrant;
        }

        Set<Long> roleIds = roles.stream().map(RoleVO::getRoleId).collect(Collectors.toSet());

        // 查询权限管理的API，并根据策略分组
        List<RolePermission> permApis = rolePermissionMapper.selectList(Wrappers.<RolePermission>lambdaQuery()
                .eq(RolePermission::getProjectId, projectId)
                .in(RolePermission::getRoleId, roleIds)
        );
        Map<PermissionPolicy, Set<Long>> groupedPermissionIds = permApis.stream().collect(Collectors.groupingBy(
                RolePermission::getPolicy,
                Collectors.mapping(RolePermission::getPermissionId, Collectors.toSet())
        ));

        // 查询allowed
        Set<Long> allowedPermissionIds = groupedPermissionIds.get(PermissionPolicy.ALLOW);
        if (CollectionUtils.isNotEmpty(allowedPermissionIds)) {
            List<Permission> allowedPermissions = permissionMapper.selectList(Wrappers.<Permission>lambdaQuery()
                    .eq(Permission::getProjectId, projectId)
                    .in(Permission::getPermissionId, allowedPermissionIds)
                    .eq(Permission::getIsOnline, true)
            );
            List<PermissionVO> allowedApiVos = EnhancedBeanUtils.createAndCopyList(allowedPermissions, PermissionVO.class);
            accountPermissionGrant.setAllowedPermissions(allowedApiVos);
        }

        // 查询refused
        Set<Long> refusedPermissionIds = groupedPermissionIds.get(PermissionPolicy.REFUSE);
        if (CollectionUtils.isNotEmpty(refusedPermissionIds)) {
            List<Permission> refusedPermissions = permissionMapper.selectList(Wrappers.<Permission>lambdaQuery()
                    .eq(Permission::getProjectId, projectId)
                    .in(Permission::getPermissionId, refusedPermissionIds)
                    .eq(Permission::getIsOnline, true)
            );
            List<PermissionVO> refusedApiVos = EnhancedBeanUtils.createAndCopyList(refusedPermissions, PermissionVO.class);
            accountPermissionGrant.setRefusedPermissions(refusedApiVos);
        }
        return accountPermissionGrant;
    }

}
