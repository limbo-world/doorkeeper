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

package org.limbo.doorkeeper.server.service;

import org.limbo.doorkeeper.server.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.server.entity.Resource;

/**
 * @author Devil
 * @date 2021/1/10 10:39 上午
 */
public class DoorkeeperService {

    private Long dkRealmId = 0L;
    private Long dkClientId = 0L;

    // 对于client为dk的来说 下面的资源是不能新增删除的 由系统定义
    //

    public void createRealm(String realmName) {
        Resource resource = new Resource();
        resource.setRealmId(dkRealmId);
        resource.setClientId(dkClientId);
        resource.setName(realmName + "-" + DoorkeeperConstants.REALM);

        // todo uri tag

        // todo realm相关权限操作
        // 1. 委托方 2. 域角色 3. 用户 4. 用户角色绑定
    }

    public void creatClient(String realmName, String clientName) {
        Resource resource = new Resource();
        resource.setRealmId(dkRealmId);
        resource.setClientId(dkClientId);
        resource.setName(realmName + "-" + clientName + "-" + DoorkeeperConstants.CLIENT);

        // todo uri tag

        // todo client相关操作
        // 1. 委托方角色 2. 资源 3. 策略 4 权限
    }

}
