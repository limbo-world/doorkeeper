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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.model.param.add.PermissionAddParam;
import org.limbo.doorkeeper.api.model.param.batch.PermissionBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.query.PermissionQueryParam;
import org.limbo.doorkeeper.api.model.param.update.PermissionUpdateParam;
import org.limbo.doorkeeper.api.model.vo.PageVO;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.limbo.doorkeeper.infrastructure.mapper.NamespaceMapper;
import org.limbo.doorkeeper.infrastructure.po.NamespacePO;
import org.limbo.doorkeeper.server.infrastructure.dao.PermissionPolicyDao;
import org.limbo.doorkeeper.server.infrastructure.dao.PermissionResourceDao;
import org.limbo.doorkeeper.server.infrastructure.exception.ParamException;
import org.limbo.doorkeeper.infrastructure.mapper.PermissionMapper;
import org.limbo.doorkeeper.infrastructure.mapper.PermissionPolicyMapper;
import org.limbo.doorkeeper.infrastructure.mapper.PermissionResourceMapper;
import org.limbo.doorkeeper.infrastructure.po.PermissionPO;
import org.limbo.doorkeeper.infrastructure.po.PermissionPolicyPO;
import org.limbo.doorkeeper.infrastructure.po.PermissionResourcePO;
import org.limbo.doorkeeper.server.infrastructure.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.infrastructure.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @since 2021/1/8 9:24 上午
 */
@Service
public class PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private NamespaceMapper namespaceMapper;

    @Autowired
    private PermissionPolicyDao permissionPolicyDao;

    @Autowired
    private PermissionResourceDao permissionResourceDao;

    @Autowired
    private PermissionPolicyMapper permissionPolicyMapper;

    @Autowired
    private PermissionResourceMapper permissionResourceMapper;

    @Transactional
    public PermissionVO add(Long realmId, Long clientId, PermissionAddParam param) {
        Verifies.notNull(param.getLogic(), "判断逻辑不存在");
        Verifies.notNull(param.getIntention(), "执行逻辑不存在");

        NamespacePO client = namespaceMapper.getById(realmId, clientId);
        Verifies.notNull(client, "委托方不存在");

        PermissionPO permission = EnhancedBeanUtils.createAndCopy(param, PermissionPO.class);
        permission.setRealmId(client.getRealmId());
        permission.setClientId(client.getNamespaceId());

        permissionMapper.insert(permission);

        permissionResourceDao.update(permission.getPermissionId(), param.getResourceIds());

        permissionPolicyDao.update(permission.getPermissionId(), param.getPolicyIds());

        return EnhancedBeanUtils.createAndCopy(permission, PermissionVO.class);
    }

    @Transactional
    public void batchUpdate(Long realmId, Long clientId, PermissionBatchUpdateParam param) {
        switch (param.getType()) {
            case UPDATE:
                if (CollectionUtils.isEmpty(param.getPermissionIds())) {
                    return;
                }
                permissionMapper.update(null, Wrappers.<PermissionPO>lambdaUpdate()
                        .set(param.getIsEnabled() != null, PermissionPO::getIsEnabled, param.getIsEnabled())
                        .in(PermissionPO::getPermissionId, param.getPermissionIds())
                        .eq(PermissionPO::getRealmId, realmId)
                        .eq(PermissionPO::getClientId, clientId)
                );
                break;
            case DELETE:
                if (CollectionUtils.isEmpty(param.getPermissionIds())) {
                    return;
                }
                List<PermissionPO> permissions = permissionMapper.selectList(Wrappers.<PermissionPO>lambdaQuery()
                        .select(PermissionPO::getPermissionId)
                        .eq(PermissionPO::getRealmId, realmId)
                        .eq(PermissionPO::getClientId, clientId)
                        .in(PermissionPO::getPermissionId, param.getPermissionIds())
                );
                if (CollectionUtils.isEmpty(permissions)) {
                    return;
                }
                List<Long> permissionIds = permissions.stream().map(PermissionPO::getPermissionId).collect(Collectors.toList());
                permissionMapper.deleteBatchIds(permissionIds);
                permissionResourceMapper.delete(Wrappers.<PermissionResourcePO>lambdaQuery()
                        .in(PermissionResourcePO::getPermissionId, permissionIds)
                );
                permissionPolicyMapper.delete(Wrappers.<PermissionPolicyPO>lambdaQuery()
                        .in(PermissionPolicyPO::getPermissionId, permissionIds)
                );
                break;
            default:
                break;
        }
    }

    public PageVO<PermissionVO> page(Long realmId, Long clientId, PermissionQueryParam param) {
        param.setRealmId(realmId);
        param.setClientId(clientId);
        long count = permissionMapper.voCount(param);

        PageVO<PermissionVO> result = PageVO.convertByPage(param);
        result.setTotal(count);
        if (count > 0) {
            result.setData(permissionMapper.getVOS(param));
        }
        return result;
    }

    public PermissionVO get(Long realmId, Long clientId, Long permissionId) {
        return permissionMapper.getVO(realmId, clientId, permissionId);
    }

    @Transactional
    public void update(Long realmId, Long clientId, Long permissionId, PermissionUpdateParam param) {
        PermissionPO permission = permissionMapper.getById(realmId, clientId, permissionId);
        Verifies.notNull(permission, "权限不存在");

        try {
            permissionMapper.update(null, Wrappers.<PermissionPO>lambdaUpdate()
                    .set(StringUtils.isNotBlank(param.getName()), PermissionPO::getName, param.getName())
                    .set(param.getDescription() != null, PermissionPO::getDescription, param.getDescription())
                    .set(param.getLogic() != null, PermissionPO::getLogic, param.getLogic())
                    .set(param.getIntention() != null, PermissionPO::getIntention, param.getIntention())
                    .set(param.getIsEnabled() != null, PermissionPO::getIsEnabled, param.getIsEnabled())
                    .set(PermissionPO::getUpdateTime, new Date())
                    .eq(PermissionPO::getRealmId, realmId)
                    .eq(PermissionPO::getClientId, clientId)
                    .eq(PermissionPO::getPermissionId, permissionId)
            );
        } catch (DuplicateKeyException e) {
            throw new ParamException("权限已存在");
        }

        permissionResourceDao.update(permissionId, param.getResourceIds());

        permissionPolicyDao.update(permissionId, param.getPolicyIds());

    }

}
