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

package org.limbo.doorkeeper.server.support.auth.policies;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.model.param.check.PolicyCheckerParam;
import org.limbo.doorkeeper.api.model.vo.GroupVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyGroupVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.dal.entity.Group;
import org.limbo.doorkeeper.server.dal.entity.GroupUser;
import org.limbo.doorkeeper.server.dal.entity.User;
import org.limbo.doorkeeper.server.dal.mapper.GroupMapper;
import org.limbo.doorkeeper.server.dal.mapper.GroupUserMapper;
import org.limbo.doorkeeper.server.support.GroupTool;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author brozen
 * @date 2021/1/18
 */
public class GroupPolicyChecker extends AbstractPolicyChecker {

    @Setter
    private GroupUserMapper groupUserMapper;

    @Setter
    private GroupMapper groupMapper;

    public GroupPolicyChecker(User user, PolicyVO policy) {
        super(user, policy);
    }


    /**
     * {@inheritDoc}<br/>
     *
     * 检查授权校验参数中的用户是否在对应用户组
     * 目前用户组有继承关系（后面考虑做成可配置的）
     *
     * @param checkerParam 授权校验参数
     * @return
     */
    @Override
    protected boolean doCheck(PolicyCheckerParam checkerParam) {
        List<Group> groups = groupMapper.selectList(Wrappers.<Group>lambdaQuery()
                .eq(Group::getRealmId, policy.getRealmId())
        );
        if (CollectionUtils.isEmpty(groups)) {
            return false;
        }
        // 获取用户用户组关系
        List<GroupUser> groupUsers = groupUserMapper.selectList(Wrappers.<GroupUser>lambdaQuery()
                .eq(GroupUser::getUserId, user.getUserId())
        );
        if (CollectionUtils.isEmpty(groupUsers)) {
            return false;
        }

        List<GroupVO> groupVOS = EnhancedBeanUtils.createAndCopyList(groups, GroupVO.class);
        // 生成组织树
        List<GroupVO> tree = GroupTool.organizeGroupTree(null, groupVOS);
        // 传递组id
        for (PolicyGroupVO policyGroup : policy.getGroups()) {
            GroupVO group = GroupTool.findGroup(policyGroup.getGroupId(), tree);
            if (group == null) {
                continue;
            }
            if (group.getGroupIds() == null) {
                group.setGroupIds(new ArrayList<>());
            }
            group.getGroupIds().add(policyGroup.getGroupId());

            if (policyGroup.getIsExtend()) {
                extendGroupId(policyGroup.getGroupId(), group.getChildren());
            }
        }
        // 用户所在组织id
        Set<Long> groupIds = new HashSet<>();
        for (GroupUser groupUser : groupUsers) {
            GroupVO group = GroupTool.findGroup(groupUser.getGroupId(), tree);
            if (group == null) {
                continue;
            }
            if (CollectionUtils.isNotEmpty(group.getGroupIds())) {
                groupIds.addAll(group.getGroupIds());
            }
        }
        // 判断用户的组id是否包含策略的组id
        for (Long userGroupId : groupIds) {
            for (PolicyGroupVO group : policy.getGroups()) {
                if (group.getGroupId().equals(userGroupId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 绑定用户组id
     */
    private void extendGroupId(Long groupId, List<GroupVO> children) {
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        for (GroupVO child : children) {
            if (child.getGroupIds() == null) {
                child.setGroupIds(new ArrayList<>());
            }
            child.getGroupIds().add(groupId);

            extendGroupId(groupId, child.getChildren());
        }
    }

}
