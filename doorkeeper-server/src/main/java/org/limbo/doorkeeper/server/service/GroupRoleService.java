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

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.model.param.add.GroupRoleAddParam;
import org.limbo.doorkeeper.api.model.param.batch.GroupRoleBatchUpdateParam;
import org.limbo.doorkeeper.api.model.vo.GroupRoleVO;
import org.limbo.doorkeeper.server.infrastructure.po.GroupRolePO;
import org.limbo.doorkeeper.server.infrastructure.mapper.GroupRoleMapper;
import org.limbo.doorkeeper.server.infrastructure.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.infrastructure.utils.MyBatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/12 3:31 下午
 */
@Service
public class GroupRoleService {

    @Autowired
    private GroupRoleMapper groupRoleMapper;

    public List<GroupRoleVO> list(Long realmId, Long groupId) {
        List<GroupRolePO> groupRoles = groupRoleMapper.selectList(Wrappers.<GroupRolePO>lambdaQuery()
                .eq(GroupRolePO::getGroupId, groupId)
        );
        return EnhancedBeanUtils.createAndCopyList(groupRoles, GroupRoleVO.class);
    }

    @Transactional
    public void batchUpdate(Long groupId, GroupRoleBatchUpdateParam param) {
        switch (param.getType()) {
            case SAVE: // 新增
                if (CollectionUtils.isEmpty(param.getRoles())) {
                    return;
                }
                List<GroupRolePO> addGroupRoles = new ArrayList<>();
                for (GroupRoleAddParam role : param.getRoles()) {
                    GroupRolePO groupRole = new GroupRolePO();
                    groupRole.setGroupId(groupId);
                    groupRole.setRoleId(role.getRoleId());
                    groupRole.setIsExtend(role.getIsExtend());
                    addGroupRoles.add(groupRole);
                }
                groupRoleMapper.batchInsertIgnore(addGroupRoles);
                break;
            case DELETE: // 删除
                if (CollectionUtils.isEmpty(param.getRoleIds())) {
                    return;
                }
                groupRoleMapper.delete(Wrappers.<GroupRolePO>lambdaQuery()
                        .eq(GroupRolePO::getGroupId, groupId)
                        .in(GroupRolePO::getRoleId, param.getRoleIds())
                );
                break;
            case UPDATE: // 更新
                if (CollectionUtils.isEmpty(param.getRoles())) {
                    return;
                }
                List<Wrapper<GroupRolePO>> updateWrappers = new ArrayList<>();
                for (GroupRoleAddParam role : param.getRoles()) {
                    updateWrappers.add(Wrappers.<GroupRolePO>lambdaUpdate()
                            .set(GroupRolePO::getIsExtend, role.getIsExtend())
                            .eq(GroupRolePO::getGroupId, groupId)
                            .eq(GroupRolePO::getRoleId, role.getRoleId())
                    );
                }
                MyBatisPlusUtils.batchUpdate(updateWrappers, GroupRolePO.class);
                break;
            default:
                break;
        }
    }

}
