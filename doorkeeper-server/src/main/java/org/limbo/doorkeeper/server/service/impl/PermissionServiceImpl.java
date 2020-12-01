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
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.*;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.limbo.doorkeeper.server.constants.BusinessType;
import org.limbo.doorkeeper.server.constants.OperateType;
import org.limbo.doorkeeper.server.dao.PermissionMapper;
import org.limbo.doorkeeper.server.dao.RolePermissionMapper;
import org.limbo.doorkeeper.server.entity.Permission;
import org.limbo.doorkeeper.server.entity.RolePermission;
import org.limbo.doorkeeper.server.service.PermissionService;
import org.limbo.doorkeeper.server.service.RolePermissionService;
import org.limbo.doorkeeper.server.support.plog.PLog;
import org.limbo.doorkeeper.server.support.plog.PLogConstants;
import org.limbo.doorkeeper.server.support.plog.PLogParam;
import org.limbo.doorkeeper.server.support.plog.PLogTag;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @date 2020/11/20 9:36 AM
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Override
    @Transactional
    @PLog(operateType = OperateType.CREATE, businessType = BusinessType.PERMISSION)
    public PermissionVO addPermission(PLogParam pLogParam, @PLogTag(PLogConstants.CONTENT) Long projectId,
                                      @PLogTag(PLogConstants.CONTENT) PermissionAddParam param) {
        Permission permission = EnhancedBeanUtils.createAndCopy(param, Permission.class);
        permission.setProjectId(projectId);
        permissionMapper.insert(permission);
        return EnhancedBeanUtils.createAndCopy(permission, PermissionVO.class);
    }

    @Override
    @Transactional
    @PLog(operateType = OperateType.UPDATE, businessType = BusinessType.PERMISSION)
    public int updatePermission(PLogParam pLogParam, @PLogTag(PLogConstants.CONTENT) Long projectId,
                                @PLogTag(PLogConstants.CONTENT) PermissionUpdateParam param) {
        return permissionMapper.update(null, Wrappers.<Permission>lambdaUpdate()
                .set(StringUtils.isNotBlank(param.getPermissionName()), Permission::getPermissionName, param.getPermissionName())
                .set(StringUtils.isNotBlank(param.getPermissionDescribe()), Permission::getPermissionDescribe, param.getPermissionDescribe())
                .set(param.getIsOnline() != null, Permission::getIsOnline, param.getIsOnline())
                .eq(Permission::getPermissionId, param.getPermissionId())
                .eq(Permission::getProjectId, projectId)
        );
    }

    @Override
    @Transactional
    @PLog(operateType = OperateType.DELETE, businessType = BusinessType.PERMISSION)
    public int deletePermission(PLogParam pLogParam, @PLogTag(PLogConstants.CONTENT) Long projectId,
                                @PLogTag(PLogConstants.CONTENT) List<Long> permissionIds) {
        // 删除角色权限
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(Wrappers.<RolePermission>lambdaQuery()
                .select(RolePermission::getRolePermissionId)
                .eq(RolePermission::getProjectId, projectId)
                .in(RolePermission::getPermissionId, permissionIds)
        );
        if (CollectionUtils.isNotEmpty(rolePermissions)) {
            rolePermissionService.deleteRolePermission(pLogParam, projectId,
                    rolePermissions.stream().map(RolePermission::getRolePermissionId).collect(Collectors.toList()));
        }

        return permissionMapper.delete(Wrappers.<Permission>lambdaQuery()
                .in(Permission::getPermissionId, permissionIds)
                .eq(Permission::getProjectId, projectId)
        );
    }

    @Override
    @Transactional
    @PLog(operateType = OperateType.UPDATE, businessType = BusinessType.PERMISSION)
    public int batchUpdate(PLogParam pLogParam, @PLogTag(PLogConstants.CONTENT) Long projectId,
                           @PLogTag(PLogConstants.CONTENT) PermissionBatchUpdateParam param) {
        return permissionMapper.update(null, Wrappers.<Permission>lambdaUpdate()
                .set(Permission::getIsOnline, param.getIsOnline())
                .in(Permission::getPermissionId, param.getPermissionIds())
                .eq(Permission::getProjectId, projectId)
        );
    }

    @Override
    public List<PermissionVO> all(Long projectId) {
        List<Permission> permissions = permissionMapper.selectList(Wrappers.<Permission>lambdaQuery()
                .eq(Permission::getProjectId, projectId)
        );
        return EnhancedBeanUtils.createAndCopyList(permissions, PermissionVO.class);
    }

    @Override
    public Page<PermissionVO> queryPage(Long projectId, PermissionQueryParam param) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Permission> mpage = MyBatisPlusUtils.pageOf(param);
        mpage = permissionMapper.selectPage(mpage, Wrappers.<Permission>lambdaQuery()
                .eq(Permission::getProjectId, projectId)
                .like(StringUtils.isNotBlank(param.getPermissionName()), Permission::getPermissionName, param.getPermissionName())
        );

        param.setTotal(mpage.getTotal());
        param.setData(EnhancedBeanUtils.createAndCopyList(mpage.getRecords(), PermissionVO.class));
        return param;
    }

}
