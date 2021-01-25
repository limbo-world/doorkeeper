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
import org.limbo.doorkeeper.api.exception.ParamException;
import org.limbo.doorkeeper.api.model.param.role.RoleAddParam;
import org.limbo.doorkeeper.api.model.param.role.RoleQueryParam;
import org.limbo.doorkeeper.api.model.param.role.RoleUpdateParam;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.limbo.doorkeeper.server.dal.mapper.RoleMapper;
import org.limbo.doorkeeper.server.dal.entity.Role;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Devil
 * @date 2021/1/4 5:29 下午
 */
@Service
public class RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Transactional
    public RoleVO add(Long realmId, Long clientId, RoleAddParam param) {
        Role role = EnhancedBeanUtils.createAndCopy(param, Role.class);
        role.setRealmId(realmId);
        role.setClientId(clientId);
        try {
            roleMapper.insert(role);
        } catch (DuplicateKeyException e) {
            throw new ParamException("角色已存在");
        }
        return EnhancedBeanUtils.createAndCopy(role, RoleVO.class);
    }

    public List<RoleVO> list(Long realmId, Long clientId, RoleQueryParam param) {
        param.setRealmId(realmId);
        param.setClientId(clientId);
        return roleMapper.listVOS(param);
    }

    public RoleVO get(Long realmId, Long clientId, Long roleId) {
        Role role = roleMapper.getById(realmId, clientId, roleId);
        Verifies.notNull(role, "角色不存在");
        return EnhancedBeanUtils.createAndCopy(role, RoleVO.class);
    }

    public void update(Long realmId, Long clientId, Long roleId, RoleUpdateParam param) {
        Role role = roleMapper.getById(realmId, clientId, roleId);
        Verifies.notNull(role, "角色不存在");

        roleMapper.update(null, Wrappers.<Role>lambdaUpdate()
                .set(Role::getDescription, param.getDescription())
                .set(Role::getIsDefault, param.getIsDefault())
                .set(Role::getIsEnabled, param.getIsEnabled())
                .eq(Role::getRoleId, roleId)
        );
    }

}
