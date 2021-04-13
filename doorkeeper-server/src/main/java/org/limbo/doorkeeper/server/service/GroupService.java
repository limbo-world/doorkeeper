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
import org.limbo.doorkeeper.api.constants.BatchMethod;
import org.limbo.doorkeeper.api.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.api.model.param.group.*;
import org.limbo.doorkeeper.api.model.vo.GroupVO;
import org.limbo.doorkeeper.server.dal.entity.Group;
import org.limbo.doorkeeper.server.dal.entity.GroupRole;
import org.limbo.doorkeeper.server.dal.entity.GroupUser;
import org.limbo.doorkeeper.server.dal.entity.Realm;
import org.limbo.doorkeeper.server.dal.entity.policy.PolicyGroup;
import org.limbo.doorkeeper.server.dal.mapper.GroupMapper;
import org.limbo.doorkeeper.server.dal.mapper.GroupRoleMapper;
import org.limbo.doorkeeper.server.dal.mapper.GroupUserMapper;
import org.limbo.doorkeeper.server.dal.mapper.RealmMapper;
import org.limbo.doorkeeper.server.dal.mapper.policy.PolicyGroupMapper;
import org.limbo.doorkeeper.server.service.policy.PolicyGroupService;
import org.limbo.doorkeeper.server.support.ParamException;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Devil
 * @date 2021/1/9 7:57 下午
 */
@Service
public class GroupService {

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private RealmMapper realmMapper;

    @Autowired
    private GroupRoleMapper groupRoleMapper;

    @Autowired
    private GroupUserMapper groupUserMapper;

    @Autowired
    private PolicyGroupMapper policyGroupMapper;

    @Autowired
    private GroupUserService groupUserService;

    @Autowired
    private GroupRoleService groupRoleService;

    @Autowired
    private PolicyGroupService policyGroupService;

    @Transactional
    public GroupVO add(Long realmId, GroupAddParam param) {
        if (param.getParentId() == null) {
            param.setParentId(DoorkeeperConstants.DEFAULT_ID);
        }
        Group group = EnhancedBeanUtils.createAndCopy(param, Group.class);
        group.setRealmId(realmId);
        try {
            groupMapper.insert(group);
        } catch (DuplicateKeyException e) {
            throw new ParamException("用户组已存在");
        }

        // 用户组用户
        if (CollectionUtils.isNotEmpty(param.getUserIds())) {
            GroupUserBatchUpdateParam batchUpdateParam  = new GroupUserBatchUpdateParam();
            batchUpdateParam.setType(BatchMethod.SAVE);
            batchUpdateParam.setUserIds(param.getUserIds());
            groupUserService.batchUpdate(group.getGroupId(), batchUpdateParam);
        }
        // 用户组策略
        if (CollectionUtils.isNotEmpty(param.getPolicies())) {
            policyGroupService.batchSave(param.getPolicies());
        }
        // 用户组角色
        if (CollectionUtils.isNotEmpty(param.getRoles())) {
            GroupRoleBatchUpdateParam batchUpdateParam  = new GroupRoleBatchUpdateParam();
            batchUpdateParam.setType(BatchMethod.SAVE);
            batchUpdateParam.setRoles(param.getRoles());
            groupRoleService.batchUpdate(group.getGroupId(), batchUpdateParam);
        }

        return EnhancedBeanUtils.createAndCopy(group, GroupVO.class);
    }

    public List<GroupVO> list(Long realmId, GroupQueryParam param) {
        List<Group> groups = groupMapper.selectList(Wrappers.<Group>lambdaQuery()
                .eq(Group::getRealmId, realmId)
                .eq(param.getParentId() != null, Group::getParentId, param.getParentId())
                .eq(StringUtils.isNotBlank(param.getName()), Group::getName, param.getName())
        );
        return EnhancedBeanUtils.createAndCopyList(groups, GroupVO.class);
    }

    public GroupVO getById(Long realmId, Long groupId) {
        Group group = groupMapper.selectOne(Wrappers.<Group>lambdaQuery()
                .eq(Group::getGroupId, groupId)
                .eq(Group::getRealmId, realmId)
        );
        return EnhancedBeanUtils.createAndCopy(group, GroupVO.class);
    }

    public GroupVO get(Long realmId, Long parentId, String name) {
        if (parentId == null) {
            parentId = DoorkeeperConstants.DEFAULT_ID;
        }
        Group group = groupMapper.selectOne(Wrappers.<Group>lambdaQuery()
                .eq(Group::getRealmId, realmId)
                .eq(Group::getParentId, parentId)
                .eq(Group::getName, name)
        );
        return EnhancedBeanUtils.createAndCopy(group, GroupVO.class);
    }

    /**
     * 获取doorkeeper下名为realm的用户组
     */
    public GroupVO getDoorkeeperRealmGroup() {
        Realm doorkeeperRealm = realmMapper.getDoorkeeperRealm();

        Group group = groupMapper.selectOne(Wrappers.<Group>lambdaQuery()
                .eq(Group::getRealmId, doorkeeperRealm.getRealmId())
                .eq(Group::getParentId, DoorkeeperConstants.DEFAULT_ID)
                .eq(Group::getName, DoorkeeperConstants.REALM)
        );
        return EnhancedBeanUtils.createAndCopy(group, GroupVO.class);
    }

    @Transactional
    public void update(Long realmId, Long groupId, GroupUpdateParam param) {
        groupMapper.update(null, Wrappers.<Group>lambdaUpdate()
                .set(param.getDescription() != null, Group::getDescription, param.getDescription())
                .set(param.getIsDefault() != null, Group::getIsDefault, param.getIsDefault())
                .set(param.getParentId() != null, Group::getParentId, param.getParentId())
                .eq(Group::getGroupId, groupId)
                .eq(Group::getRealmId, realmId)
        );
    }

    @Transactional
    public void delete(Long realmId, Long groupId) {
        Integer num = groupMapper.selectCount(Wrappers.<Group>lambdaQuery()
                .eq(Group::getGroupId, groupId)
                .eq(Group::getRealmId, realmId)
        );
        if (num <= 0) {
            return;
        }
        // 如果还有子节点，需要先删除子节点
        num = groupMapper.selectCount(Wrappers.<Group>lambdaQuery()
                .eq(Group::getParentId, groupId)
                .eq(Group::getRealmId, realmId)
        );
        Verifies.verify(num <= 0, "用户组还有子节点，请先删除");

        groupMapper.deleteById(groupId);
        groupRoleMapper.delete(Wrappers.<GroupRole>lambdaQuery()
                .eq(GroupRole::getGroupId, groupId)
        );
        groupUserMapper.delete(Wrappers.<GroupUser>lambdaQuery()
                .eq(GroupUser::getGroupId, groupId)
        );
        policyGroupMapper.delete(Wrappers.<PolicyGroup>lambdaQuery()
                .eq(PolicyGroup::getGroupId, groupId)
        );
    }
}
