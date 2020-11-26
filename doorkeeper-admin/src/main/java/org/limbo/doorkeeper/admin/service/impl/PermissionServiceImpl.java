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
import org.limbo.doorkeeper.admin.model.param.PermissionAddParam;
import org.limbo.doorkeeper.admin.model.param.PermissionUpdateParam;
import org.limbo.doorkeeper.admin.service.PermissionService;
import org.limbo.doorkeeper.admin.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.api.client.PermissionApiClient;
import org.limbo.doorkeeper.api.client.PermissionClient;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.PermissionApiAddParam;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Devil
 * @date 2020/11/26 9:45 AM
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionClient permissionClient;

    @Autowired
    private PermissionApiClient permissionApiClient;

    @Override
    public Response<PermissionVO> add(PermissionAddParam param) {

        org.limbo.doorkeeper.api.model.param.PermissionAddParam permission = EnhancedBeanUtils.createAndCopy(param, org.limbo.doorkeeper.api.model.param.PermissionAddParam.class);
        Response<PermissionVO> permissionVOResponse = permissionClient.add(permission);
        if (!permissionVOResponse.ok()) {
            return permissionVOResponse;
        }

        if (CollectionUtils.isNotEmpty(param.getPermissionApis())) {
            PermissionVO permissionVO = permissionVOResponse.getData();
            for (PermissionApiAddParam permissionApiAddParam : param.getPermissionApis()) {
                permissionApiAddParam.setPermissionId(permissionVO.getPermissionId());
            }
            permissionApiClient.addPermissionApi(param.getPermissionApis());
        }

        return permissionVOResponse;
    }

    @Override
    public Response<Integer> update(Long permissionId, PermissionUpdateParam param) {
        org.limbo.doorkeeper.api.model.param.PermissionUpdateParam permission = EnhancedBeanUtils.createAndCopy(param, org.limbo.doorkeeper.api.model.param.PermissionUpdateParam.class);

        Response<Integer> update = permissionClient.update(permissionId, permission);

        // 删除权限
        if (CollectionUtils.isNotEmpty(param.getDeletePermissionApiIds())) {
            permissionApiClient.deletePermissionApi(param.getDeletePermissionApiIds());
        }

        // 新增权限
        if (CollectionUtils.isNotEmpty(param.getAddPermissionApis())) {
            for (PermissionApiAddParam permissionApiAddParam : param.getAddPermissionApis()) {
                permissionApiAddParam.setPermissionId(permissionId);
            }
            permissionApiClient.addPermissionApi(param.getAddPermissionApis());
        }

        return update;
    }
}
