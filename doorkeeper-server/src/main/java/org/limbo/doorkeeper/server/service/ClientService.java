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

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.api.model.param.check.ResourceCheckParam;
import org.limbo.doorkeeper.api.model.param.client.ClientAddParam;
import org.limbo.doorkeeper.api.model.param.client.ClientQueryParam;
import org.limbo.doorkeeper.api.model.param.client.ClientUpdateParam;
import org.limbo.doorkeeper.api.model.vo.ClientVO;
import org.limbo.doorkeeper.api.model.vo.ResourceTagVO;
import org.limbo.doorkeeper.api.model.vo.ResourceVO;
import org.limbo.doorkeeper.api.model.vo.check.ResourceCheckResult;
import org.limbo.doorkeeper.server.dal.entity.Client;
import org.limbo.doorkeeper.server.dal.entity.Realm;
import org.limbo.doorkeeper.server.dal.mapper.ClientMapper;
import org.limbo.doorkeeper.server.dal.mapper.RealmMapper;
import org.limbo.doorkeeper.server.support.ParamException;
import org.limbo.doorkeeper.server.support.auth.ResourceChecker;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/4 2:59 下午
 */
@Service
public class ClientService {

    @Autowired
    private RealmMapper realmMapper;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private DoorkeeperService doorkeeperService;

    @Autowired
    private ResourceChecker resourceChecker;

    @Transactional
    public ClientVO add(Long realmId, Long userId, ClientAddParam param) {
        Client client = EnhancedBeanUtils.createAndCopy(param, Client.class);
        client.setRealmId(realmId);
        try {
            clientMapper.insert(client);
        } catch (DuplicateKeyException e) {
            throw new ParamException("委托方已存在");
        }

        // 初始化client数据
        doorkeeperService.createClientResource(userId, realmId, client.getClientId(), client.getName());

        return EnhancedBeanUtils.createAndCopy(client, ClientVO.class);
    }

    /**
     * user拥有哪些client
     */
    public List<ClientVO> userClients(Long realmId, Long userId, ClientQueryParam param) {
        List<String> clientNames = null;
        // 判断是不是doorkeeper的REALM admin
        if (!doorkeeperService.isSuperAdmin(userId)) {
            clientNames = new ArrayList<>();

            Realm realm = realmMapper.selectById(realmId);
            Realm doorkeeperRealm = realmMapper.getDoorkeeperRealm();
            // 获取realm在doorkeeper下对应的client
            Client apiClient = clientMapper.getByName(doorkeeperRealm.getRealmId(), DoorkeeperConstants.API_CLIENT);

            ResourceCheckParam checkParam = new ResourceCheckParam();
            checkParam.setClientId(apiClient.getClientId());
            checkParam.setTags(Collections.singletonList("type=clientOwn"));
            ResourceCheckResult check = resourceChecker.check(userId, true, checkParam);
            if (CollectionUtils.isEmpty(check.getResources())) {
                return new ArrayList<>();
            }

            for (ResourceVO resource : check.getResources()) {
                if (org.apache.commons.collections4.CollectionUtils.isEmpty(resource.getTags())) {
                    continue;
                }
                for (ResourceTagVO tag : resource.getTags()) {
                    if (DoorkeeperConstants.CLIENT_ID.equals(tag.getK())) {
                        clientNames.add(tag.getV());
                        break;
                    }
                }
            }
        }

        List<Client> clients = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                .eq(Client::getRealmId, realmId)
                .eq(StringUtils.isNotBlank(param.getName()), Client::getName, param.getName())
                .like(StringUtils.isNotBlank(param.getDimName()), Client::getName, param.getDimName())
                .in(clientNames != null, Client::getName, clientNames)
                .orderByDesc(Client::getClientId)
        );
        return EnhancedBeanUtils.createAndCopyList(clients, ClientVO.class);
    }

    public ClientVO get(Long realmId, Long clientId) {
        Client client = clientMapper.getById(realmId, clientId);
        Verifies.notNull(client, "委托方不存在");
        return EnhancedBeanUtils.createAndCopy(client, ClientVO.class);
    }

    @Transactional
    public void update(Long realmId, Long clientId, ClientUpdateParam param) {
        Client client = clientMapper.getById(realmId, clientId);
        Verifies.notNull(client, "委托方不存在");

        try {
            clientMapper.update(null, Wrappers.<Client>lambdaUpdate()
                    .set(StringUtils.isNotBlank(param.getName()) && !client.getName().equals(param.getName()),
                            Client::getName, param.getName())
                    .set(param.getDescription() != null, Client::getDescription, param.getDescription())
                    .set(param.getIsEnabled() != null, Client::getIsEnabled, param.getIsEnabled())
                    .eq(Client::getClientId, clientId)
            );
        } catch (DuplicateKeyException e) {
            throw new ParamException("名称已存在");
        }

    }

}
