/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   	http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.limbo.doorkeeper.server.dal.dao;

import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.limbo.doorkeeper.server.dal.mapper.PermissionMapper;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Devil
 * @date 2021/1/25 10:38 上午
 */
@Component
public class PermissionDao {

    @Autowired
    private PermissionMapper permissionMapper;

    public List<PermissionVO> getVOSByPermissionIds(Long realmId, Long clientId,
                                                    List<Long> permissionIds, Boolean isEnabled) {
        Verifies.notNull(realmId, "域不能为空");
        Verifies.notNull(clientId, "委托方不能为空");
        Verifies.notNull(permissionIds, "权限ID列表不能为空");
        return permissionMapper.getVOS(realmId, clientId, permissionIds, isEnabled);
    }

}
