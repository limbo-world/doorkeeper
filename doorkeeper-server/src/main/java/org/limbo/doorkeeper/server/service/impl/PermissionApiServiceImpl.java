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

import org.limbo.doorkeeper.api.model.param.PermissionApiAddParam;
import org.limbo.doorkeeper.server.dao.PermissionApiMapper;
import org.limbo.doorkeeper.server.service.PermissionApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/20 9:48 AM
 */
@Service
public class PermissionApiServiceImpl implements PermissionApiService {

    @Autowired
    private PermissionApiMapper permissionApiMapper;

    @Override
    public void addPermissionApi(List<PermissionApiAddParam> permissionApis) {
        permissionApiMapper.batchInsertOrIgnore(permissionApis);
    }

    @Override
    public int deletePermissionApi(List<Long> permissionApiIds) {
        return permissionApiMapper.deleteBatchIds(permissionApiIds);
    }
}
