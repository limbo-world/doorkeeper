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
import org.limbo.doorkeeper.api.model.param.PermissionAddParam;
import org.limbo.doorkeeper.api.model.param.PermissionUpdateParam;
import org.limbo.doorkeeper.server.dao.PermissionMapper;
import org.limbo.doorkeeper.server.entity.Permission;
import org.limbo.doorkeeper.server.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/20 9:36 AM
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public void addPermission(PermissionAddParam param) {

    }

    @Override
    public void updatePermission(PermissionUpdateParam param) {

    }

    @Override
    public void deletePermission(List<Long> permissionIds) {
        permissionMapper.update(null, Wrappers.<Permission>lambdaUpdate()
                .set(Permission::getIsDeleted, true)
                .in(Permission::getPermissionId, permissionIds)
        );
    }

    @Override
    public void permissionOnline(List<Long> permissionIds) {
        permissionMapper.update(null, Wrappers.<Permission>lambdaUpdate()
                .set(Permission::getIsOnline, true)
                .in(Permission::getPermissionId, permissionIds)
        );
    }

    @Override
    public void permissionOffline(List<Long> permissionIds) {
        permissionMapper.update(null, Wrappers.<Permission>lambdaUpdate()
                .set(Permission::getIsOnline, false)
                .in(Permission::getPermissionId, permissionIds)
        );
    }

}
