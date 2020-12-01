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

package org.limbo.doorkeeper.server.service;

import org.limbo.doorkeeper.api.model.param.RolePermissionAddParam;
import org.limbo.doorkeeper.api.model.param.RolePermissionQueryParam;
import org.limbo.doorkeeper.api.model.vo.RolePermissionVO;
import org.limbo.doorkeeper.server.support.plog.PLogParam;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/20 11:08 AM
 */
public interface RolePermissionService {
    /**
     * 查询列表
     */
    List<RolePermissionVO> list(Long projectId, RolePermissionQueryParam param);
    /**
     * 为角色添加权限
     */
    void addRolePermission(PLogParam pLogParam, Long projectId, List<RolePermissionAddParam> params);

    /**
     * 删除角色权限
     */
    int deleteRolePermission(PLogParam pLogParam, Long projectId, List<Long> rolePermissionIds);
}
