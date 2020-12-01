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
import org.limbo.doorkeeper.api.model.param.ApiCheckParam;
import org.limbo.doorkeeper.api.model.param.PermissionCheckParam;
import org.limbo.doorkeeper.api.model.param.RoleCheckParam;
import org.limbo.doorkeeper.api.model.vo.*;
import org.limbo.doorkeeper.server.dao.*;
import org.limbo.doorkeeper.server.entity.*;
import org.limbo.doorkeeper.server.service.AuthenticationService;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.*;
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

    @Autowired
    private PermissionApiMapper permissionApiMapper;

    @Autowired
    private ApiMapper apiMapper;

    @Override
    public Boolean accessApiAllowed(Long projectId, ApiCheckParam param) {
        Account account = accountMapper.getProjectAccountById(projectId, param.getAccountId());
        if (account == null) {
            return false;
        }

        // 管理员拥有全部权限
        if (account.getIsAdmin()) {
            return true;
        }

        // 拿到用户全部API
        AccountApiGrantVO grantedApis = _this.getGrantedApis(projectId, account.getAccountId());
        // 如果没有授权信息 或 授权访问的API列表为空，则禁止访问
        if (grantedApis == null || CollectionUtils.isEmpty(grantedApis.getAllowedApis())) {
            return false;
        }

        // 首先检测refusedApis
        List<ApiVO> refusedApis = grantedApis.getRefusedApis();
        if (CollectionUtils.isNotEmpty(refusedApis)) {
            // 如果存在请求方法与请求路径匹配，说明禁止访问
            if (refusedApis.stream().anyMatch(api -> apiMatch(api, param))) {
                return false;
            }
        }

        // 然后检测allowedApis
        List<ApiVO> allowedApis = grantedApis.getAllowedApis();
        // 如果存在请求方法与请求路径匹配，说明允许访问
        return allowedApis.stream().anyMatch(api -> apiMatch(api, param));
    }

    @Override
    public Boolean accessPermissionAllowed(Long projectId, PermissionCheckParam param) {
        Account account = accountMapper.getProjectAccountById(projectId, param.getAccountId());
        if (account == null) {
            return false;
        }

        // 管理员拥有全部权限
        if (account.getIsAdmin()) {
            return true;
        }

        List<PermissionVO> grantedPermissions = _this.getGrantedPermissions(projectId, account.getAccountId());
        if (CollectionUtils.isEmpty(grantedPermissions)) {
            return false;
        }

        return grantedPermissions.stream().anyMatch(permission -> permission.getPermissionId().equals(param.getPermissionId()));
    }

    @Override
    public Boolean accessRoleAllowed(Long projectId, RoleCheckParam param) {
        Account account = accountMapper.getProjectAccountById(projectId, param.getAccountId());
        if (account == null) {
            return false;
        }

        // 管理员拥有全部权限
        if (account.getIsAdmin()) {
            return true;
        }

        List<RoleVO> grantedRoles = _this.getGrantedRoles(projectId, account.getAccountId());
        if (CollectionUtils.isEmpty(grantedRoles)) {
            return false;
        }

        return grantedRoles.stream().anyMatch(role -> role.getRoleId().equals(param.getRoleId()));
    }

    /**
     * 检测API配置与请求检查参数的请求url是否匹配，检测method和path两部分
     */
    private boolean apiMatch(ApiVO api, ApiCheckParam param) {
        AntPathMatcher antPathMatcher = PATH_MATCHER.get();
        return methodMatch(api.getApiMethod(), param.getMethod())
                && antPathMatcher.match(api.getApiUrl(), param.getPath());
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
    public AccountGrantVO getGrantInfo(Long projectId, Long accountId) {
        Account account = accountMapper.getProjectAccountById(projectId, accountId);
        Verifies.notNull(account, "账户不存在");

        return AccountGrantVO.builder()
                .accountId(accountId)
                .roles(_this.getGrantedRoles(projectId, accountId))
                .permissions(_this.getGrantedPermissions(projectId, accountId))
                .build();
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
        if (CollectionUtils.isEmpty(accountRoles)) {
            return Collections.emptyList();
        }

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
    public AccountApiGrantVO getGrantedApis(Long projectId, Long accountId) {
        AccountApiGrantVO accountApiGrant = new AccountApiGrantVO();
        accountApiGrant.setAccountId(accountId);
        accountApiGrant.setAllowedApis(new ArrayList<>());
        accountApiGrant.setRefusedApis(new ArrayList<>());

        // 查询用户授权的所有权限
        List<PermissionVO> permissions = _this.getGrantedPermissions(projectId, accountId);
        if (CollectionUtils.isEmpty(permissions)) {
            return accountApiGrant;
        }

        Set<Long> permIds = permissions.stream().map(PermissionVO::getPermissionId).collect(Collectors.toSet());

        // 查询权限管理的API，并根据策略分组
        List<PermissionApi> permApis = permissionApiMapper.selectList(Wrappers.<PermissionApi>lambdaQuery()
                .eq(PermissionApi::getProjectId, projectId)
                .in(PermissionApi::getPermissionId, permIds)
        );
        if (CollectionUtils.isEmpty(permApis)) {
            return accountApiGrant;
        }
        Map<PermissionPolicy, Set<Long>> groupedApiIds = permApis.stream().collect(Collectors.groupingBy(
                PermissionApi::getPolicy,
                Collectors.mapping(PermissionApi::getApiId, Collectors.toSet())
        ));

        // 查询allowed
        Set<Long> allowedApiIds = groupedApiIds.get(PermissionPolicy.ALLOW);
        if (CollectionUtils.isNotEmpty(allowedApiIds)) {
            List<Api> allowedApis = apiMapper.selectList(Wrappers.<Api>lambdaQuery()
                    .eq(Api::getProjectId, projectId)
                    .in(Api::getApiId, allowedApiIds)
            );
            List<ApiVO> allowedApiVos = EnhancedBeanUtils.createAndCopyList(allowedApis, ApiVO.class);
            accountApiGrant.setAllowedApis(allowedApiVos);
        }

        // 查询refused
        Set<Long> refusedApiIds = groupedApiIds.get(PermissionPolicy.REFUSE);
        if (CollectionUtils.isNotEmpty(refusedApiIds)) {
            List<Api> refusedApis = apiMapper.selectList(Wrappers.<Api>lambdaQuery()
                    .eq(Api::getProjectId, projectId)
                    .in(Api::getApiId, refusedApiIds)
            );
            List<ApiVO> refusedApiVos = EnhancedBeanUtils.createAndCopyList(refusedApis, ApiVO.class);
            accountApiGrant.setRefusedApis(refusedApiVos);
        }
        return accountApiGrant;
    }

}
