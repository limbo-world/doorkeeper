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
import org.limbo.doorkeeper.api.model.vo.policy.PolicyRoleVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.server.dal.mapper.GroupMapper;
import org.limbo.doorkeeper.server.dal.mapper.GroupRoleMapper;
import org.limbo.doorkeeper.server.dal.mapper.GroupUserMapper;
import org.limbo.doorkeeper.server.dal.mapper.UserRoleMapper;
import org.limbo.doorkeeper.server.dal.entity.Group;
import org.limbo.doorkeeper.server.dal.entity.GroupRole;
import org.limbo.doorkeeper.server.dal.entity.GroupUser;
import org.limbo.doorkeeper.server.dal.entity.UserRole;
import org.limbo.doorkeeper.api.model.param.auth.AuthorizationCheckParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author brozen
 * @date 2021/1/18
 */
public class RolePolicyChecker extends AbstractPolicyChecker {

    @Setter
    private UserRoleMapper userRoleMapper;

    @Setter
    private GroupUserMapper groupUserMapper;

    @Setter
    private GroupRoleMapper groupRoleMapper;

    @Setter
    private GroupMapper groupMapper;

    public RolePolicyChecker(PolicyVO policy) {
        super(policy);
    }

    @Override
    protected boolean doCheck(AuthorizationCheckParam<?> authorizationCheckParam) {
        // 策略绑定的角色 去除未启用的
        List<Long> roleIds = policy.getRoles().stream()
                .filter(PolicyRoleVO::getIsEnabled)
                .map(PolicyRoleVO::getRoleId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(roleIds)) {
            return false;
        }

        // 用户直接绑定的角色
        List<UserRole> userRoles = userRoleMapper.selectList(Wrappers.<UserRole>lambdaQuery()
                .eq(UserRole::getUserId, authorizationCheckParam.getUserId())
                .in(UserRole::getRoleId, roleIds)
        );
        Set<Long> userRoleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());

        // 用户所在用户组的角色
        List<GroupUser> groupUsers = groupUserMapper.selectList(Wrappers.<GroupUser>lambdaQuery()
                .eq(GroupUser::getUserId, authorizationCheckParam.getUserId())
        );
        if (CollectionUtils.isNotEmpty(groupUsers)) {
            List<Long> groupIds = userGroupTreeIds(authorizationCheckParam.getUserId());
            List<GroupRole> groupRoles = groupRoleMapper.selectList(Wrappers.<GroupRole>lambdaQuery()
                    .select(GroupRole::getRoleId)
                    .in(GroupRole::getGroupId, groupIds)
                    .in(GroupRole::getRoleId, roleIds)
            );
            if (CollectionUtils.isNotEmpty(groupRoles)) {
                userRoleIds.addAll(groupRoles.stream().map(GroupRole::getRoleId).collect(Collectors.toList()));
            }
        }


        // 解析策略逻辑，判断是否满足逻辑条件
        return getPolicyLogic().isSatisfied(roleIds.size(), userRoleIds.size());
    }

    /**
     * 用户组id 包含父级 比如 用户属于组 A11 获取 A -> A1 -> A11 3 个
     */
    private List<Long> userGroupTreeIds(Long userId) {
        // 用户在哪些组
        List<GroupUser> groupUsers = groupUserMapper.selectList(Wrappers.<GroupUser>lambdaQuery()
                .eq(GroupUser::getUserId, userId)
        );
        if (CollectionUtils.isEmpty(groupUsers)) {
            return new ArrayList<>();
        }
        List<Long> groupIds = groupUsers.stream().map(GroupUser::getGroupId).collect(Collectors.toList());
        List<Group> groups = groupMapper.selectList(Wrappers.<Group>lambdaQuery()
                .in(Group::getGroupId, groupIds)
        );
        if (CollectionUtils.isEmpty(groups)) {
            return new ArrayList<>();
        }

        // 循环获取用户组织结构 比如 用户属于组 A11 获取 A -> A1 -> A11 3 个
        List<Long> parentIds = groups.stream().map(Group::getParentId)
                .filter(id -> !DoorkeeperConstants.DEFAULT_ID.equals(id)).collect(Collectors.toList());
        int i = 0; // 防止死循环
        while (i < 10) {
            if (CollectionUtils.isEmpty(parentIds)) {
                break;
            }
            List<Group> parents = groupMapper.selectList(Wrappers.<Group>lambdaQuery()
                    .in(Group::getGroupId, parentIds)
            );
            if (CollectionUtils.isEmpty(parents)) {
                break;
            }
            groups.addAll(parents);

            // 如果父节点为 0 则已经是顶级节点 排除掉
            parentIds = parents.stream().map(Group::getParentId)
                    .filter(id -> !DoorkeeperConstants.DEFAULT_ID.equals(id)).collect(Collectors.toList());
            i++;
        }
        return groups.stream().map(Group::getGroupId).collect(Collectors.toList());
    }

}
