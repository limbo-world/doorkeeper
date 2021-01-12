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
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.exception.ParamException;
import org.limbo.doorkeeper.api.model.param.client.ClientAddParam;
import org.limbo.doorkeeper.api.model.param.client.ClientQueryParam;
import org.limbo.doorkeeper.api.model.param.client.ClientUpdateParam;
import org.limbo.doorkeeper.api.model.vo.ClientVO;
import org.limbo.doorkeeper.server.dao.ClientMapper;
import org.limbo.doorkeeper.server.entity.Client;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Devil
 * @date 2021/1/4 2:59 下午
 */
@Service
public class ClientService {

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private DoorkeeperService doorkeeperService;

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
//        doorkeeperService.creatClient(userId, client.getClientId(), client.getName());

        return EnhancedBeanUtils.createAndCopy(client, ClientVO.class);
    }

    /**
     * user拥有哪些client
     */
    public List<ClientVO> userClients(Long realmId, Long userId, ClientQueryParam param) {
        // todo
//        Set<Long> clientIds = userClients.stream().map(UserClient::getClientId).collect(Collectors.toSet());
        List<Client> clients = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                .eq(Client::getRealmId, realmId)
                .eq(StringUtils.isNotBlank(param.getName()), Client::getName, param.getName())
                .like(StringUtils.isNotBlank(param.getDimName()), Client::getName, param.getDimName())
//                .in(Client::getClientId, new ArrayList<>())
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

        clientMapper.update(null, Wrappers.<Client>lambdaUpdate()
                .set(param.getDescription() != null, Client::getDescription, param.getDescription())
                .set(param.getIsEnabled() != null, Client::getIsEnabled, param.getIsEnabled())
                .eq(Client::getClientId, clientId)
        );
    }

}
