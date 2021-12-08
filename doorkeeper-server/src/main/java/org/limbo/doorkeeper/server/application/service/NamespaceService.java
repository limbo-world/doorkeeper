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

package org.limbo.doorkeeper.server.application.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.dto.param.update.ClientUpdateParam;
import org.limbo.doorkeeper.api.dto.vo.NamespaceVO;
import org.limbo.doorkeeper.server.infrastructure.exception.ParamException;
import org.limbo.doorkeeper.server.infrastructure.mapper.NamespaceMapper;
import org.limbo.doorkeeper.server.infrastructure.po.NamespacePO;
import org.limbo.doorkeeper.server.infrastructure.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.infrastructure.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Devil
 * @since 2021/1/4 2:59 下午
 */
@Service
public class NamespaceService {

    @Autowired
    private NamespaceMapper namespaceMapper;

    public NamespaceVO get(Long realmId, Long clientId) {
        NamespacePO client = namespaceMapper.getById(realmId, clientId);
        Verifies.notNull(client, "委托方不存在");
        return EnhancedBeanUtils.createAndCopy(client, NamespaceVO.class);
    }

    @Transactional
    public void update(Long realmId, Long clientId, ClientUpdateParam param) {
        NamespacePO client = namespaceMapper.getById(realmId, clientId);
        Verifies.notNull(client, "委托方不存在");

        try {
            namespaceMapper.update(null, Wrappers.<NamespacePO>lambdaUpdate()
                    .set(StringUtils.isNotBlank(param.getName()), NamespacePO::getName, param.getName())
                    .set(param.getDescription() != null, NamespacePO::getDescription, param.getDescription())
                    .set(param.getIsEnabled() != null, NamespacePO::getIsEnabled, param.getIsEnabled())
                    .eq(NamespacePO::getNamespaceId, clientId)
            );
        } catch (DuplicateKeyException e) {
            throw new ParamException("名称已存在");
        }

    }

    public List<NamespaceVO> list(Long realmId) {
        List<NamespacePO> clients = namespaceMapper.selectList(Wrappers.<NamespacePO>lambdaQuery()
                .eq(NamespacePO::getRealmId, realmId)
                .orderByDesc(NamespacePO::getNamespaceId)
        );
        return EnhancedBeanUtils.createAndCopyList(clients, NamespaceVO.class);
    }

}
