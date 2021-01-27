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
import org.limbo.doorkeeper.api.model.param.auth.AuthorizationCheckParam;
import org.limbo.doorkeeper.api.model.vo.GroupRoleVO;
import org.limbo.doorkeeper.api.model.vo.GroupVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyRoleVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.dal.entity.*;
import org.limbo.doorkeeper.server.dal.mapper.*;
import org.limbo.doorkeeper.server.support.GroupTool;
import org.limbo.doorkeeper.server.support.auth.checker.LogicChecker;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;

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

    @Setter
    private RoleMapper roleMapper;

    public RolePolicyChecker(PolicyVO policy) {
        super(policy);
    }

    /**
     * 获取用户角色  以及用户所在用户组角色（目前用户组角色是继承的，后面可以做成可配置的方式）
     *
     * @param authorizationCheckParam 授权校验参数
     * @return
     */
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
        if (CollectionUtils.isEmpty(userRoles)) {
            userRoles = new ArrayList<>();
        }
        Set<Long> userRoleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());

        // 用户所在用户组的角色
        userRoleIds.addAll(userGroupRoleIds(authorizationCheckParam.getUserId()));

        if (CollectionUtils.isEmpty(userRoleIds)) {
            return false;
        }

        // 获取策略角色
        userRoleIds = userRoleIds.stream().filter(roleIds::contains).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(userRoleIds)) {
            return false;
        }

        List<Role> roles = roleMapper.selectList(Wrappers.<Role>lambdaQuery()
                .eq(Role::getIsEnabled, true)
                .in(Role::getRoleId, userRoleIds)
        );

        // 解析策略逻辑，判断是否满足逻辑条件
        return LogicChecker.isSatisfied(getPolicyLogic(), roleIds.size(), roles == null ? 0 : roles.size());
    }

    /**
     * 用户所在用户组的角色id
     */
    private List<Long> userGroupRoleIds(Long userId) {
        List<Group> groups = groupMapper.selectList(Wrappers.<Group>lambdaQuery()
                .eq(Group::getRealmId, policy.getRealmId())
        );
        if (CollectionUtils.isEmpty(groups)) {
            return new ArrayList<>();
        }
        // 获取用户组角色
        List<GroupRole> groupRoles = groupRoleMapper.selectList(Wrappers.<GroupRole>lambdaQuery()
                .in(GroupRole::getGroupId, groups.stream().map(Group::getGroupId).collect(Collectors.toSet()))
        );
        if (CollectionUtils.isEmpty(groupRoles)) {
            return new ArrayList<>();
        }
        // 获取用户用户组关系
        List<GroupUser> groupUsers = groupUserMapper.selectList(Wrappers.<GroupUser>lambdaQuery()
                .eq(GroupUser::getUserId, userId)
        );
        if (CollectionUtils.isEmpty(groupUsers)) {
            return new ArrayList<>();
        }

        List<GroupVO> groupVOS = EnhancedBeanUtils.createAndCopyList(groups, GroupVO.class);
        List<GroupRoleVO> groupRoleVOS = EnhancedBeanUtils.createAndCopyList(groupRoles, GroupRoleVO.class);

        // 生成组织树
        List<GroupVO> tree = GroupTool.organizeGroupTree(null, groupVOS);
        // 给树绑定角色
        for (GroupRoleVO groupRoleVO : groupRoleVOS) {
            GroupVO group = GroupTool.findGroup(groupRoleVO.getGroupId(), tree);
            if (group == null) {
                continue;
            }
            if (group.getRoles() == null) {
                group.setRoles(new ArrayList<>());
            }
            group.getRoles().add(groupRoleVO);

            if (groupRoleVO.getIsExtend()) {
                extendGroupRole(groupRoleVO, group.getChildren());
            }
        }
        // 获取用户组织角色
        List<Long> roleIds = new ArrayList<>();
        for (GroupUser groupUser : groupUsers) {
            GroupVO group = GroupTool.findGroup(groupUser.getGroupId(), tree);
            if (group == null) {
                continue;
            }
            if (CollectionUtils.isNotEmpty(group.getRoles())) {
                roleIds.addAll(group.getRoles().stream().map(GroupRoleVO::getRoleId).collect(Collectors.toList()));
            }
        }
        return roleIds;
    }

    private void extendGroupRole(GroupRoleVO groupRoleVO, List<GroupVO> children) {
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        for (GroupVO child : children) {
            if (child.getRoles() == null) {
                child.setRoles(new ArrayList<>());
            }
            child.getRoles().add(groupRoleVO);

            extendGroupRole(groupRoleVO, child.getChildren());
        }
    }

}
