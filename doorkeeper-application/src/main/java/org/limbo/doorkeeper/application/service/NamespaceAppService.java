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

package org.limbo.doorkeeper.application.service;

import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.dto.param.add.NamespaceAddParam;
import org.limbo.doorkeeper.api.dto.param.update.ClientUpdateParam;
import org.limbo.doorkeeper.api.dto.vo.NamespaceVO;
import org.limbo.doorkeeper.core.domian.aggregate.Namespace;
import org.limbo.doorkeeper.infrastructure.constants.MsgConstants;
import org.limbo.doorkeeper.infrastructure.dao.NamespaceDao;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.NamespaceMapper;
import org.limbo.doorkeeper.infrastructure.po.NamespacePO;
import org.limbo.utils.reflection.EnhancedBeanUtils;
import org.limbo.utils.verifies.Verifies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Devil
 * @since 2021/1/4 2:59 下午
 */
@Service
public class NamespaceAppService {

    @Resource
    private NamespaceMapper namespaceMapper;

    @Resource
    private NamespaceDao namespaceDao;

    public Long create(Long userId, Long realmId, NamespaceAddParam param) {
        Namespace namespace = Namespace.builder()
                .name(param.getName())
                .description(param.getDescription())
                .isEnabled(param.getIsEnabled())
                .build();

        Long namespaceId = namespaceRepository.save(realmId, namespace);

        // 创建资源 绑定用户

        return namespaceId;
    }

    public NamespaceVO get(Long namespaceId) {
        NamespacePO namespace = namespaceDao.getById(namespaceId);
        Verifies.notNull(namespace, MsgConstants.NO_NAMESPACE);
        return EnhancedBeanUtils.createAndCopy(namespace, NamespaceVO.class);
    }

    @Transactional
    public void update(Long namespaceId, ClientUpdateParam param) {
        NamespacePO namespace = namespaceDao.getById(namespaceId);
        Verifies.notNull(namespace, MsgConstants.NO_NAMESPACE);

        if (StringUtils.isNotBlank(param.getName())) {
            namespace.setName(param.getName());
        }
        if (param.getDescription() != null) {
            namespace.setDescription(param.getDescription());
        }
        if (param.getIsEnabled() != null) {
            namespace.setIsEnabled(param.getIsEnabled());
        }
        namespaceDao.updateById(namespace);
    }

    public List<NamespaceVO> list(Long realmId) {
        List<NamespacePO> namespaceDTOS = namespaceDao.listByRealm(realmId);
        return EnhancedBeanUtils.createAndCopyList(namespaceDTOS, NamespaceVO.class);
    }

}
