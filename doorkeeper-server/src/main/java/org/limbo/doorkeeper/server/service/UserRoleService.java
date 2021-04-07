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
import org.limbo.doorkeeper.api.model.param.check.RoleCheckParam;
import org.limbo.doorkeeper.api.model.param.user.UserRoleBatchUpdateParam;
import org.limbo.doorkeeper.api.model.vo.GroupRoleVO;
import org.limbo.doorkeeper.api.model.vo.GroupVO;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.limbo.doorkeeper.api.model.vo.UserRoleVO;
import org.limbo.doorkeeper.server.dal.entity.*;
import org.limbo.doorkeeper.server.dal.mapper.*;
import org.limbo.doorkeeper.server.support.GroupTool;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @date 2021/1/12 3:31 下午
 */
@Service
public class UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private GroupRoleMapper groupRoleMapper;

    @Autowired
    private GroupUserMapper groupUserMapper;

    public List<UserRoleVO> list(Long realmId, Long userId) {
        List<UserRole> userRoles = userRoleMapper.selectList(Wrappers.<UserRole>lambdaQuery()
                .eq(UserRole::getUserId, userId)
        );
        return EnhancedBeanUtils.createAndCopyList(userRoles, UserRoleVO.class);
    }

    @Transactional
    public void batchUpdate(Long userId, UserRoleBatchUpdateParam param) {
        switch (param.getType()) {
            case SAVE: // 新增
                List<UserRole> userRoles = new ArrayList<>();
                for (Long roleId : param.getRoleIds()) {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    userRoles.add(userRole);
                }
                MyBatisPlusUtils.batchSave(userRoles, UserRole.class);
                break;
            case DELETE: // 删除
                userRoleMapper.delete(Wrappers.<UserRole>lambdaQuery()
                        .eq(UserRole::getUserId, userId)
                        .in(UserRole::getRoleId, param.getRoleIds())
                );
                break;
            default:
                break;
        }
    }

    /**
     * 获取用户拥有的特定角色
     * @param userId
     * @param realmId
     * @param param
     * @return
     */
    public List<RoleVO> checkRole(Long userId, Long realmId, RoleCheckParam param) {
        // 根据查询条件获取角色id
        List<Role> roles = roleMapper.selectList(Wrappers.<Role>lambdaQuery()
                .select(Role::getRoleId)
                .eq(Role::getRealmId, realmId)
                .eq(Role::getIsEnabled, true)
                .in(CollectionUtils.isNotEmpty(param.getRoleIds()), Role::getRoleId, param.getRoleIds())
                .eq(param.getClientId() != null, Role::getClientId, param.getClientId())
                .eq(StringUtils.isNotBlank(param.getName()), Role::getName, param.getName())
                .like(StringUtils.isNotBlank(param.getDimName()), Role::getName, param.getDimName())
                .in(CollectionUtils.isNotEmpty(param.getNames()), Role::getName, param.getNames())
        );

        if (CollectionUtils.isEmpty(roles)) {
            return new ArrayList<>();
        }

        Set<Long> roleIds = roles.stream().map(Role::getRoleId).collect(Collectors.toSet());

        // 获取用户角色
        List<UserRole> userRoles = userRoleMapper.selectList(Wrappers.<UserRole>lambdaQuery()
                .eq(UserRole::getUserId, userId)
                .in(UserRole::getRoleId, roleIds)
        );

        if (CollectionUtils.isEmpty(userRoles)) {
            userRoles = new ArrayList<>();
        }

        // 用户拥有的角色ID
        Set<Long> userOwnedRoleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());

        // 用户所在用户组的角色
        List<Long> userGroupRoleIds = getUserGroupRoleIds(userId, realmId, roleIds);
        userOwnedRoleIds.addAll(userGroupRoleIds);

        if (CollectionUtils.isEmpty(userOwnedRoleIds)) {
            return new ArrayList<>();
        }

        roles = roleMapper.selectBatchIds(userOwnedRoleIds);
        if (CollectionUtils.isEmpty(roles)) {
            return new ArrayList<>();
        }

        return EnhancedBeanUtils.createAndCopyList(roles, RoleVO.class);
    }

    /**
     * 用户所在用户组的角色id
     * @param userId
     * @param realmId
     * @param checkedRoleIds 期望的角色id
     * @return
     */
    private List<Long> getUserGroupRoleIds(Long userId, Long realmId, Collection<Long> checkedRoleIds) {
        // 获取用户所在用户组
        List<Group> groups = groupMapper.selectList(Wrappers.<Group>lambdaQuery()
                .eq(Group::getRealmId, realmId)
        );
        if (CollectionUtils.isEmpty(groups)) {
            return new ArrayList<>();
        }
        // 获取用户组角色
        List<GroupRole> groupRoles = groupRoleMapper.selectList(Wrappers.<GroupRole>lambdaQuery()
                .in(GroupRole::getGroupId, groups.stream().map(Group::getGroupId).collect(Collectors.toSet()))
                .in(GroupRole::getRoleId, checkedRoleIds)
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
