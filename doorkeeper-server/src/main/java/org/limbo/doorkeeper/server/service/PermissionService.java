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
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.permission.PermissionAddParam;
import org.limbo.doorkeeper.api.model.param.permission.PermissionBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.permission.PermissionQueryParam;
import org.limbo.doorkeeper.api.model.param.permission.PermissionUpdateParam;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.limbo.doorkeeper.server.dal.entity.*;
import org.limbo.doorkeeper.server.dal.mapper.ClientMapper;
import org.limbo.doorkeeper.server.dal.mapper.PermissionMapper;
import org.limbo.doorkeeper.server.dal.mapper.PermissionPolicyMapper;
import org.limbo.doorkeeper.server.dal.mapper.PermissionResourceMapper;
import org.limbo.doorkeeper.server.support.ParamException;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @date 2021/1/8 9:24 上午
 */
@Service
public class PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private PermissionPolicyService permissionPolicyService;

    @Autowired
    private PermissionResourceService permissionResourceService;

    @Autowired
    private PermissionPolicyMapper permissionPolicyMapper;

    @Autowired
    private PermissionResourceMapper permissionResourceMapper;

    @Transactional
    public PermissionVO add(Long realmId, Long clientId, PermissionAddParam param) {
        Verifies.notNull(param.getLogic(), "判断逻辑不存在");
        Verifies.notNull(param.getIntention(), "执行逻辑不存在");

        Client client = clientMapper.selectOne(Wrappers.<Client>lambdaQuery()
                .eq(Client::getRealmId, realmId)
                .eq(Client::getClientId, clientId)
        );
        Verifies.notNull(client, "委托方不存在");

        Permission permission = EnhancedBeanUtils.createAndCopy(param, Permission.class);
        permission.setRealmId(client.getRealmId());
        permission.setClientId(client.getClientId());

        permissionMapper.insert(permission);

        permissionResourceService.update(permission.getPermissionId(), param.getResourceIds());

        permissionPolicyService.update(permission.getPermissionId(), param.getPolicyIds());

        return EnhancedBeanUtils.createAndCopy(permission, PermissionVO.class);
    }

    @Transactional
    public void batchUpdate(Long realmId, Long clientId, PermissionBatchUpdateParam param) {
        switch (param.getType()) {
            case UPDATE:
                if (CollectionUtils.isEmpty(param.getPermissionIds())) {
                    return;
                }
                permissionMapper.update(null, Wrappers.<Permission>lambdaUpdate()
                        .set(param.getIsEnabled() != null, Permission::getIsEnabled, param.getIsEnabled())
                        .in(Permission::getPermissionId, param.getPermissionIds())
                        .eq(Permission::getRealmId, realmId)
                        .eq(Permission::getClientId, clientId)
                );
                break;
            case DELETE:
                if (CollectionUtils.isEmpty(param.getPermissionIds())) {
                    return;
                }
                List<Permission> permissions = permissionMapper.selectList(Wrappers.<Permission>lambdaQuery()
                        .select(Permission::getPermissionId)
                        .eq(Permission::getRealmId, realmId)
                        .eq(Permission::getClientId, clientId)
                        .in(Permission::getPermissionId, param.getPermissionIds())
                );
                if (CollectionUtils.isEmpty(permissions)) {
                    return;
                }
                List<Long> permissionIds = permissions.stream().map(Permission::getPermissionId).collect(Collectors.toList());
                permissionMapper.deleteBatchIds(permissionIds);
                permissionResourceMapper.delete(Wrappers.<PermissionResource>lambdaQuery()
                        .in(PermissionResource::getPermissionId, permissionIds)
                );
                permissionPolicyMapper.delete(Wrappers.<PermissionPolicy>lambdaQuery()
                        .in(PermissionPolicy::getPermissionId, permissionIds)
                );
                break;
            default:
                break;
        }
    }

    public Page<PermissionVO> page(Long realmId, Long clientId, PermissionQueryParam param) {
        param.setRealmId(realmId);
        param.setClientId(clientId);
        long count = permissionMapper.voCount(param);
        param.setTotal(count);
        if (count > 0) {
            param.setData(permissionMapper.getVOS(param));
        }
        return param;
    }

    public PermissionVO get(Long realmId, Long clientId, Long permissionId) {
        return permissionMapper.getVO(realmId, clientId, permissionId);
    }

    @Transactional
    public void update(Long realmId, Long clientId, Long permissionId, PermissionUpdateParam param) {
        Permission permission = permissionMapper.getById(realmId, clientId, permissionId);
        Verifies.notNull(permission, "权限不存在");

        try {
            permissionMapper.update(null, Wrappers.<Permission>lambdaUpdate()
                    .set(StringUtils.isNotBlank(param.getName()) && !permission.getName().equals(param.getName()),
                            Permission::getName, param.getName())
                    .set(param.getDescription() != null, Permission::getDescription, param.getDescription())
                    .set(param.getLogic() != null, Permission::getLogic, param.getLogic())
                    .set(param.getIntention() != null, Permission::getIntention, param.getIntention())
                    .set(param.getIsEnabled() != null, Permission::getIsEnabled, param.getIsEnabled())
                    .set(Permission::getUpdateTime, new Date())
                    .eq(Permission::getRealmId, realmId)
                    .eq(Permission::getClientId, clientId)
                    .eq(Permission::getPermissionId, permissionId)
            );
        } catch (DuplicateKeyException e) {
            throw new ParamException("权限已存在");
        }

        permissionResourceService.update(permissionId, param.getResourceIds());

        permissionPolicyService.update(permissionId, param.getPolicyIds());

    }

}
