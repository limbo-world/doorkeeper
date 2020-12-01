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
import org.limbo.doorkeeper.api.model.param.RolePermissionAddParam;
import org.limbo.doorkeeper.api.model.param.RolePermissionQueryParam;
import org.limbo.doorkeeper.api.model.vo.RolePermissionVO;
import org.limbo.doorkeeper.server.constants.BusinessType;
import org.limbo.doorkeeper.server.constants.OperateType;
import org.limbo.doorkeeper.server.dao.RolePermissionMapper;
import org.limbo.doorkeeper.server.entity.RolePermission;
import org.limbo.doorkeeper.server.service.RolePermissionService;
import org.limbo.doorkeeper.server.support.plog.PLog;
import org.limbo.doorkeeper.server.support.plog.PLogConstants;
import org.limbo.doorkeeper.server.support.plog.PLogParam;
import org.limbo.doorkeeper.server.support.plog.PLogTag;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/20 11:09 AM
 */
@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public List<RolePermissionVO> list(Long projectId, RolePermissionQueryParam param) {
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(Wrappers.<RolePermission>lambdaQuery()
                .eq(RolePermission::getProjectId, projectId)
                .eq(param.getRoleId() != null, RolePermission::getRoleId, param.getRoleId())
        );
        return EnhancedBeanUtils.createAndCopyList(rolePermissions, RolePermissionVO.class);
    }

    @Override
    @Transactional
    @PLog(operateType = OperateType.CREATE, businessType = BusinessType.ROLE_PERMISSION)
    public void addRolePermission(PLogParam pLogParam, @PLogTag(PLogConstants.CONTENT) Long projectId,
                                  @PLogTag(PLogConstants.CONTENT) List<RolePermissionAddParam> params) {
        if (CollectionUtils.isEmpty(params)) {
            return;
        }
        List<RolePermission> rolePermissions = EnhancedBeanUtils.createAndCopyList(params, RolePermission.class);
        for (RolePermission rolePermission : rolePermissions) {
            rolePermission.setProjectId(projectId);
        }
        rolePermissionMapper.batchInsertIgnore(rolePermissions);
    }

    @Override
    @PLog(operateType = OperateType.DELETE, businessType = BusinessType.ROLE_PERMISSION)
    public int deleteRolePermission(PLogParam pLogParam, @PLogTag(PLogConstants.CONTENT) Long projectId,
                                    @PLogTag(PLogConstants.CONTENT) List<Long> rolePermissionIds) {
        return rolePermissionMapper.delete(Wrappers.<RolePermission>lambdaQuery()
                .eq(RolePermission::getProjectId, projectId)
                .in(RolePermission::getRolePermissionId, rolePermissionIds)
        );
    }
}
