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
import org.limbo.doorkeeper.api.model.param.add.GroupAddParam;
import org.limbo.doorkeeper.api.model.param.add.PolicyGroupAddParam;
import org.limbo.doorkeeper.api.model.param.query.GroupQueryParam;
import org.limbo.doorkeeper.api.model.param.batch.GroupRoleBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.update.GroupUpdateParam;
import org.limbo.doorkeeper.api.model.param.batch.GroupUserBatchUpdateParam;
import org.limbo.doorkeeper.api.model.vo.GroupVO;
import org.limbo.doorkeeper.server.infrastructure.po.*;
import org.limbo.doorkeeper.server.infrastructure.po.PolicyGroupPO;
import org.limbo.doorkeeper.server.infrastructure.mapper.GroupMapper;
import org.limbo.doorkeeper.server.infrastructure.mapper.GroupRoleMapper;
import org.limbo.doorkeeper.server.infrastructure.mapper.GroupUserMapper;
import org.limbo.doorkeeper.server.infrastructure.mapper.RealmMapper;
import org.limbo.doorkeeper.server.infrastructure.mapper.policy.PolicyGroupMapper;
import org.limbo.doorkeeper.server.infrastructure.dao.PolicyGroupDao;
import org.limbo.doorkeeper.server.infrastructure.exception.ParamException;
import org.limbo.doorkeeper.server.infrastructure.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.infrastructure.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
    private PolicyGroupDao policyGroupDao;

    @Transactional
    public GroupVO add(Long realmId, GroupAddParam param) {
        if (param.getParentId() == null) {
            param.setParentId(DoorkeeperConstants.DEFAULT_ID);
        }
        GroupPO group = EnhancedBeanUtils.createAndCopy(param, GroupPO.class);
        group.setRealmId(realmId);
        try {
            groupMapper.insert(group);
        } catch (DuplicateKeyException e) {
            throw new ParamException("用户组已存在");
        }

        // 用户组用户
        if (CollectionUtils.isNotEmpty(param.getUserIds())) {
            GroupUserBatchUpdateParam batchUpdateParam = new GroupUserBatchUpdateParam();
            batchUpdateParam.setType(BatchMethod.SAVE);
            batchUpdateParam.setUserIds(param.getUserIds());
            groupUserService.batchUpdate(group.getGroupId(), batchUpdateParam);
        }
        // 用户组策略
        if (CollectionUtils.isNotEmpty(param.getPolicies())) {
            for (PolicyGroupAddParam policy : param.getPolicies()) {
                policy.setGroupId(group.getGroupId());
                policyGroupDao.batchSave(policy.getPolicyId(), Collections.singletonList(policy));
            }
        }
        // 用户组角色
        if (CollectionUtils.isNotEmpty(param.getRoles())) {
            GroupRoleBatchUpdateParam batchUpdateParam = new GroupRoleBatchUpdateParam();
            batchUpdateParam.setType(BatchMethod.SAVE);
            batchUpdateParam.setRoles(param.getRoles());
            groupRoleService.batchUpdate(group.getGroupId(), batchUpdateParam);
        }

        return EnhancedBeanUtils.createAndCopy(group, GroupVO.class);
    }

    public List<GroupVO> list(Long realmId, GroupQueryParam param) {
        List<GroupPO> groups = groupMapper.selectList(Wrappers.<GroupPO>lambdaQuery()
                .eq(GroupPO::getRealmId, realmId)
                .eq(param.getParentId() != null, GroupPO::getParentId, param.getParentId())
                .eq(StringUtils.isNotBlank(param.getName()), GroupPO::getName, param.getName())
        );
        return EnhancedBeanUtils.createAndCopyList(groups, GroupVO.class);
    }

    public GroupVO getById(Long realmId, Long groupId) {
        GroupPO group = groupMapper.selectOne(Wrappers.<GroupPO>lambdaQuery()
                .eq(GroupPO::getGroupId, groupId)
                .eq(GroupPO::getRealmId, realmId)
        );
        return EnhancedBeanUtils.createAndCopy(group, GroupVO.class);
    }

    public GroupVO get(Long realmId, Long parentId, String name) {
        if (parentId == null) {
            parentId = DoorkeeperConstants.DEFAULT_ID;
        }
        GroupPO group = groupMapper.selectOne(Wrappers.<GroupPO>lambdaQuery()
                .eq(GroupPO::getRealmId, realmId)
                .eq(GroupPO::getParentId, parentId)
                .eq(GroupPO::getName, name)
        );
        return EnhancedBeanUtils.createAndCopy(group, GroupVO.class);
    }

    @Transactional
    public void update(Long realmId, Long groupId, GroupUpdateParam param) {
        GroupPO group = groupMapper.selectById(groupId);
        Verifies.notNull(group, "用户组不存在");

        try {
            groupMapper.update(null, Wrappers.<GroupPO>lambdaUpdate()
                    .set(StringUtils.isNotBlank(param.getName()), GroupPO::getName, param.getName())
                    .set(param.getDescription() != null, GroupPO::getDescription, param.getDescription())
                    .set(param.getIsDefault() != null, GroupPO::getIsDefault, param.getIsDefault())
                    .set(param.getParentId() != null, GroupPO::getParentId, param.getParentId())
                    .eq(GroupPO::getGroupId, groupId)
                    .eq(GroupPO::getRealmId, realmId)
            );
        } catch (DuplicateKeyException e) {
            throw new ParamException("用户组已存在");
        }
    }

    @Transactional
    public void delete(Long realmId, Long groupId) {
        Integer num = groupMapper.selectCount(Wrappers.<GroupPO>lambdaQuery()
                .eq(GroupPO::getGroupId, groupId)
                .eq(GroupPO::getRealmId, realmId)
        );
        if (num <= 0) {
            return;
        }
        // 如果还有子节点，需要先删除子节点
        num = groupMapper.selectCount(Wrappers.<GroupPO>lambdaQuery()
                .eq(GroupPO::getParentId, groupId)
                .eq(GroupPO::getRealmId, realmId)
        );
        Verifies.verify(num <= 0, "用户组还有子节点，请先删除");

        groupMapper.deleteById(groupId);
        groupRoleMapper.delete(Wrappers.<GroupRolePO>lambdaQuery()
                .eq(GroupRolePO::getGroupId, groupId)
        );
        groupUserMapper.delete(Wrappers.<GroupUserPO>lambdaQuery()
                .eq(GroupUserPO::getGroupId, groupId)
        );
        policyGroupMapper.delete(Wrappers.<PolicyGroupPO>lambdaQuery()
                .eq(PolicyGroupPO::getGroupId, groupId)
        );
    }
}
