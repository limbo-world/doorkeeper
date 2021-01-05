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

package org.limbo.doorkeeper.server.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.model.param.ClientAddParam;
import org.limbo.doorkeeper.api.model.param.ClientQueryParam;
import org.limbo.doorkeeper.api.model.param.ClientUpdateParam;
import org.limbo.doorkeeper.api.model.vo.ClientVO;
import org.limbo.doorkeeper.server.dao.ClientMapper;
import org.limbo.doorkeeper.server.entity.Client;
import org.limbo.doorkeeper.server.service.ClientService;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Devil
 * @date 2021/1/4 2:59 下午
 */
@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientMapper clientMapper;

    @Override
    @Transactional
    public ClientVO add(ClientAddParam param) {
        // todo 用户是否能操作这个realm

        Client client = EnhancedBeanUtils.createAndCopy(param, Client.class);
        clientMapper.insert(client);
        return EnhancedBeanUtils.createAndCopy(client, ClientVO.class);
    }

    @Override
    public List<ClientVO> list(ClientQueryParam param) {
        // todo 返回用户能操作的
        List<Client> clients = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                .eq(Client::getRealmId, param.getRealmId())
                .eq(StringUtils.isNotBlank(param.getName()), Client::getName, param.getName())
                .like(StringUtils.isNotBlank(param.getDimName()), Client::getName, param.getDimName())
                .orderByDesc(Client::getClientId)
        );
        return EnhancedBeanUtils.createAndCopyList(clients, ClientVO.class);
    }

    @Override
    public ClientVO get(Long clientId) {
        Client client = clientMapper.selectById(clientId);
        return EnhancedBeanUtils.createAndCopy(client, ClientVO.class);
    }

    @Override
    @Transactional
    public void update(Long clientId, ClientUpdateParam param) {
        clientMapper.update(null, Wrappers.<Client>lambdaUpdate()
                .set(param.getDescription() != null, Client::getDescription, param.getDescription())
                .set(param.getIsEnabled() != null, Client::getIsEnabled, param.getIsEnabled())
                .eq(Client::getClientId, clientId)
        );
    }

}
