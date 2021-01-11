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

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.limbo.doorkeeper.api.constants.UserBindType;
import org.limbo.doorkeeper.server.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.server.dao.ClientMapper;
import org.limbo.doorkeeper.server.dao.RealmMapper;
import org.limbo.doorkeeper.server.dao.UserClientMapper;
import org.limbo.doorkeeper.server.dao.UserRealmMapper;
import org.limbo.doorkeeper.server.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Devil
 * @date 2021/1/10 10:39 上午
 */
@Service
public class DoorkeeperService {

    @Autowired
    private RealmMapper realmMapper;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private UserRealmMapper userRealmMapper;

    @Autowired
    private UserClientMapper userClientMapper;

    @Transactional
    public void createRealm(Long userId, Long realmId, String realmName) {
        Realm realm = realmMapper.selectOne(Wrappers.<Realm>lambdaQuery()
                .eq(Realm::getName, DoorkeeperConstants.REALM_NAME)
        );

        Client client = new Client();
        client.setRealmId(realm.getRealmId());
        client.setName(realmName);
        client.setIsEnabled(true);
        clientMapper.insert(client);

        // 绑定用户realm关系
        UserRealm userRealm = new UserRealm();
        userRealm.setUserId(userId);
        userRealm.setRealmId(realmId);
        userRealm.setType(UserBindType.OWNER);
        userRealmMapper.insert(userRealm);

        // todo realm相关权限操作
        // 1. 委托方 2. 域角色 3. 用户 4. 用户角色绑定
    }

    @Transactional
    public void creatClient(Long userId, Long clientId, String clientName) {
        // 绑定用户client关系
        UserClient userClient = new UserClient();
        userClient.setUserId(userId);
        userClient.setClientId(clientId);
        userClient.setType(UserBindType.OWNER);
        userClientMapper.insert(userClient);

        // todo client相关操作
        // 1. 委托方角色 2. 资源 3. 策略 4 权限
    }

}
