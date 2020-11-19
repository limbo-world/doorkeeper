/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.limbo.doorkeeper.server.service.impl;

import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.RoleAddParam;
import org.limbo.doorkeeper.api.model.param.RoleUpdateParam;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.limbo.doorkeeper.server.dao.RoleMapper;
import org.limbo.doorkeeper.server.entity.Role;
import org.limbo.doorkeeper.server.service.RoleService;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/19 5:44 PM
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    @Transactional
    public Long addRole(RoleAddParam param) {
        Role role = EnhancedBeanUtils.createAndCopy(param, Role.class);
        roleMapper.insert(role);
        Long roleId = role.getRoleId();

        // todo 角色对应有哪些权限

        return roleId;
    }

    @Override
    public Integer updateRole(RoleUpdateParam param) {
        // todo
        return null;
    }

    @Override
    public Integer deleteRole(Long projectId, Long roleId) {
        // todo
        return null;
    }

    @Override
    public List<RoleVO> listRole(Long projectId) {
        // todo
        return null;
    }

    @Override
    public Page<RoleVO> queryRole(Page<RoleVO> param) {
        // todo
        return null;
    }

    @Override
    public RoleVO getRole(Long projectId, Long roleId) {
        // todo
        return null;
    }

}
