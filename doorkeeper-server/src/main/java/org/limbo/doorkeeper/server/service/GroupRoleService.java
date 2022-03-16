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
import org.limbo.doorkeeper.api.dto.param.add.GroupRoleAddParam;
import org.limbo.doorkeeper.api.dto.param.update.GroupRoleUpdateParam;
import org.limbo.doorkeeper.api.dto.vo.GroupRoleVO;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.GroupRoleMapper;
import org.limbo.doorkeeper.server.infrastructure.po.GroupRolePO;
import org.limbo.utils.reflection.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Devil
 * @since 2021/1/12 3:31 下午
 */
@Service
public class GroupRoleService {

    @Autowired
    private GroupRoleMapper groupRoleMapper;

    public List<GroupRoleVO> list(Long groupId) {
        List<GroupRolePO> groupRoles = groupRoleMapper.selectList(Wrappers.<GroupRolePO>lambdaQuery()
                .eq(GroupRolePO::getGroupId, groupId)
        );
        return EnhancedBeanUtils.createAndCopyList(groupRoles, GroupRoleVO.class);
    }

    @Transactional
    public void add(Long groupId, GroupRoleAddParam param) {
        GroupRolePO groupRole = new GroupRolePO();
        groupRole.setGroupId(groupId);
        groupRole.setRoleId(param.getRoleId());
        groupRole.setIsExtend(param.getIsExtend());
        groupRoleMapper.insert(groupRole);
    }

    @Transactional
    public void batchDelete(Long groupId, List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }
        groupRoleMapper.delete(Wrappers.<GroupRolePO>lambdaQuery()
                .eq(GroupRolePO::getGroupId, groupId)
                .in(GroupRolePO::getRoleId, roleIds)
        );
    }

    @Transactional
    public void delete(Long groupId, Long roleId) {
        groupRoleMapper.delete(Wrappers.<GroupRolePO>lambdaQuery()
                .eq(GroupRolePO::getGroupId, groupId)
                .eq(GroupRolePO::getRoleId, roleId)
        );
    }

    @Transactional
    public void patch(Long groupId, Long roleId, GroupRoleUpdateParam param) {
        groupRoleMapper.update(null, Wrappers.<GroupRolePO>lambdaUpdate()
                .set(GroupRolePO::getIsExtend, param.getIsExtend())
                .eq(GroupRolePO::getGroupId, groupId)
                .eq(GroupRolePO::getRoleId, roleId)
        );
    }

}
