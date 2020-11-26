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

package org.limbo.doorkeeper.admin.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.admin.model.param.AdminRoleAddParam;
import org.limbo.doorkeeper.admin.model.param.AdminRoleUpdateParam;
import org.limbo.doorkeeper.admin.service.RoleService;
import org.limbo.doorkeeper.admin.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.api.client.RoleClient;
import org.limbo.doorkeeper.api.client.RolePermissionClient;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.RoleAddParam;
import org.limbo.doorkeeper.api.model.param.RolePermissionAddParam;
import org.limbo.doorkeeper.api.model.param.RoleUpdateParam;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Devil
 * @date 2020/11/26 11:19 AM
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleClient roleClient;

    @Autowired
    private RolePermissionClient rolePermissionClient;

    @Override
    public Response<RoleVO> add(AdminRoleAddParam param) {
        RoleAddParam roleAddParam = EnhancedBeanUtils.createAndCopy(param, RoleAddParam.class);

        Response<RoleVO> add = roleClient.add(roleAddParam);
        if (!add.ok()) {
            return add;
        }

        if (CollectionUtils.isNotEmpty(param.getRolePermissions())) {
            RoleVO roleVO = add.getData();
            for (RolePermissionAddParam rolePermissionAddParam : param.getRolePermissions()) {
                rolePermissionAddParam.setRoleId(roleVO.getRoleId());
            }
            rolePermissionClient.addRolePermission(param.getRolePermissions());
        }

        return add;
    }

    @Override
    public Response<Integer> update(Long roleId, AdminRoleUpdateParam param) {
        RoleUpdateParam updateParam = EnhancedBeanUtils.createAndCopy(param, RoleUpdateParam.class);
        Response<Integer> update = roleClient.update(roleId, updateParam);

        if (CollectionUtils.isNotEmpty(param.getAddRolePermissions())) {
            for (RolePermissionAddParam rolePermissionAddParam : param.getAddRolePermissions()) {
                rolePermissionAddParam.setRoleId(roleId);
            }
            rolePermissionClient.addRolePermission(param.getAddRolePermissions());
        }

        if (CollectionUtils.isNotEmpty(param.getDeleteRolePermissionIds())) {
            rolePermissionClient.deleteRolePermission(param.getDeleteRolePermissionIds());
        }

        return update;
    }

}
