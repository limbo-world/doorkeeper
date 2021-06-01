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

package org.limbo.doorkeeper.server.support.auth;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.model.param.query.RoleCheckParam;
import org.limbo.doorkeeper.api.model.vo.GroupRoleVO;
import org.limbo.doorkeeper.api.model.vo.GroupVO;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.limbo.doorkeeper.api.model.vo.check.RoleCheckResult;
import org.limbo.doorkeeper.server.domain.GroupTreeDO;
import org.limbo.doorkeeper.server.infrastructure.exception.AuthorizationException;
import org.limbo.doorkeeper.server.infrastructure.mapper.*;
import org.limbo.doorkeeper.server.infrastructure.po.*;
import org.limbo.doorkeeper.server.infrastructure.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色校验器
 *
 * @author Devil
 * @date 2021/4/19 4:21 下午
 */
@Slf4j
@Component
public class RoleChecker {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private GroupRoleMapper groupRoleMapper;

    @Autowired
    private GroupUserMapper groupUserMapper;

    /**
     * 获取用户拥有的角色
     */
    public RoleCheckResult check(Long userId, RoleCheckParam checkParam) {
        RoleCheckResult result = new RoleCheckResult();
        result.setRoles(new ArrayList<>());

        UserPO user = userMapper.selectById(userId);
        if (user == null) {
            throw new AuthorizationException("无法找到用户，Id=" + userId);
        }
        if (!user.getIsEnabled()) {
            return result;
        }

        // 根据查询条件获取角色id
        List<RolePO> roles = roleMapper.selectList(Wrappers.<RolePO>lambdaQuery()
                .select(RolePO::getRoleId)
                .eq(RolePO::getRealmId, user.getRealmId())
                .eq(RolePO::getIsEnabled, true)
                .in(CollectionUtils.isNotEmpty(checkParam.getRoleIds()), RolePO::getRoleId, checkParam.getRoleIds())
                .eq(checkParam.getClientId() != null, RolePO::getClientId, checkParam.getClientId())
                .eq(StringUtils.isNotBlank(checkParam.getName()), RolePO::getName, checkParam.getName())
                .like(StringUtils.isNotBlank(checkParam.getDimName()), RolePO::getName, checkParam.getDimName())
                .in(CollectionUtils.isNotEmpty(checkParam.getNames()), RolePO::getName, checkParam.getNames())
        );

        if (CollectionUtils.isEmpty(roles)) {
            return result;
        }

        Set<Long> roleIds = roles.stream().map(RolePO::getRoleId).collect(Collectors.toSet());

        // 获取用户角色
        List<UserRolePO> userRoles = userRoleMapper.selectList(Wrappers.<UserRolePO>lambdaQuery()
                .eq(UserRolePO::getUserId, userId)
                .in(UserRolePO::getRoleId, roleIds)
        );

        if (CollectionUtils.isEmpty(userRoles)) {
            userRoles = new ArrayList<>();
        }

        // 用户拥有的角色ID
        Set<Long> userOwnedRoleIds = userRoles.stream()
                .map(UserRolePO::getRoleId)
                .collect(Collectors.toSet());

        // 用户所在用户组的角色
        List<Long> userGroupRoleIds = getUserGroupRoleIds(userId, user.getRealmId(), roleIds);
        userOwnedRoleIds.addAll(userGroupRoleIds);

        if (CollectionUtils.isEmpty(userOwnedRoleIds)) {
            return result;
        }

        roles = roleMapper.selectBatchIds(userOwnedRoleIds);
        if (CollectionUtils.isEmpty(roles)) {
            return result;
        }

        result.setRoles(EnhancedBeanUtils.createAndCopyList(roles, RoleVO.class));
        return result;
    }

    /**
     * 用户所在用户组的角色id
     *
     * @param userId
     * @param realmId
     * @param checkedRoleIds 期望的角色id
     * @return
     */
    public List<Long> getUserGroupRoleIds(Long userId, Long realmId, Collection<Long> checkedRoleIds) {
        // 获取用户所在用户组
        List<GroupPO> groups = groupMapper.selectList(Wrappers.<GroupPO>lambdaQuery()
                .eq(GroupPO::getRealmId, realmId)
        );
        if (CollectionUtils.isEmpty(groups)) {
            return new ArrayList<>();
        }
        // 获取用户组角色
        List<GroupRolePO> groupRoles = groupRoleMapper.selectList(Wrappers.<GroupRolePO>lambdaQuery()
                .in(GroupRolePO::getGroupId, groups.stream().map(GroupPO::getGroupId).collect(Collectors.toSet()))
                .in(GroupRolePO::getRoleId, checkedRoleIds)
        );
        if (CollectionUtils.isEmpty(groupRoles)) {
            return new ArrayList<>();
        }
        // 获取用户用户组关系
        List<GroupUserPO> groupUsers = groupUserMapper.selectList(Wrappers.<GroupUserPO>lambdaQuery()
                .eq(GroupUserPO::getUserId, userId)
        );
        if (CollectionUtils.isEmpty(groupUsers)) {
            return new ArrayList<>();
        }

        List<GroupVO> groupVOS = EnhancedBeanUtils.createAndCopyList(groups, GroupVO.class);
        List<GroupRoleVO> groupRoleVOS = EnhancedBeanUtils.createAndCopyList(groupRoles, GroupRoleVO.class);

        // 生成组织树
        GroupTreeDO groupTreeDO = GroupTreeDO.create(groupVOS);
        // 给树绑定角色
        for (GroupRoleVO groupRoleVO : groupRoleVOS) {
            GroupVO group = groupTreeDO.findFromTree(groupRoleVO.getGroupId());
            if (group == null) {
                continue;
            }
            if (group.getRoles() == null) {
                group.setRoles(new ArrayList<>());
            }
            group.getRoles().add(groupRoleVO);
            // 用户组角色是否向下继承
            if (groupRoleVO.getIsExtend()) {
                extendGroupRole(groupRoleVO, group.getChildren());
            }
        }
        // 获取用户组织角色
        List<Long> roleIds = new ArrayList<>();
        for (GroupUserPO groupUser : groupUsers) {
            GroupVO group = groupTreeDO.findFromTree(groupUser.getGroupId());
            if (group == null) {
                continue;
            }
            if (CollectionUtils.isNotEmpty(group.getRoles())) {
                roleIds.addAll(group.getRoles().stream().map(GroupRoleVO::getRoleId).collect(Collectors.toList()));
            }
        }
        return roleIds;
    }

    /**
     * 给当前用户组下所有子节点增加对应角色
     */
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
