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
import org.limbo.doorkeeper.api.model.param.RoleQueryParam;
import org.limbo.doorkeeper.api.model.param.RoleUpdateParam;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.limbo.doorkeeper.server.dao.RoleMapper;
import org.limbo.doorkeeper.server.entity.Role;
import org.limbo.doorkeeper.server.service.RoleService;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
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
        return role.getRoleId();

        // todo 角色对应有哪些权限
    }

    @Override
    public Integer updateRole(RoleUpdateParam param) {
        Role role = roleMapper.getRole(param.getProjectId(), param.getRoleId());
        Verifies.notNull(role, "角色不存在");

        EnhancedBeanUtils.copyPropertiesIgnoreNull(param, role);
        return roleMapper.updateById(role);
    }

    @Override
    public Integer deleteRole(Long projectId, Long roleId) {
        Role role = roleMapper.getRole(projectId, roleId);
        Verifies.notNull(role, "角色不存在");

        return roleMapper.deleteRole(projectId, roleId);
    }

    @Override
    public List<RoleVO> listRole(Long projectId) {
        List<Role> roles = roleMapper.getRoles(projectId);
        return EnhancedBeanUtils.createAndCopyList(roles, RoleVO.class);
    }

    @Override
    public Page<RoleVO> queryRole(RoleQueryParam param) {
        // 总数，若参数中total为正数，则不需要查询
        long total = param.getTotal();
        total = total < 0 ? roleMapper.countRole(param) : total;
        param.setTotal(total);

        // 分页数据
        List<Role> roles = roleMapper.queryRole(param);
        param.setData(EnhancedBeanUtils.createAndCopyList(roles, RoleVO.class));
        return param;
    }

    @Override
    public RoleVO getRole(Long projectId, Long roleId) {
        Role role = roleMapper.getRole(projectId, roleId);
        Verifies.notNull(role, "角色不存在");

        return EnhancedBeanUtils.createAndCopy(role, RoleVO.class);
    }

}
