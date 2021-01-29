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
import org.limbo.doorkeeper.api.model.param.user.UserRoleQueryParam;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.limbo.doorkeeper.api.model.vo.UserRoleVO;
import org.limbo.doorkeeper.server.dal.entity.Role;
import org.limbo.doorkeeper.server.dal.entity.UserRole;
import org.limbo.doorkeeper.server.dal.mapper.RoleMapper;
import org.limbo.doorkeeper.server.dal.mapper.UserRoleMapper;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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

    public List<UserRoleVO> list(Long realmId, Long userId, UserRoleQueryParam param) {
        param.setRealmId(realmId);
        param.setUserId(userId);
        return userRoleMapper.listUserRoleVOS(param);
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

    public List<RoleVO> checkRole(Long userId, Long realmId, RoleCheckParam param) {
        List<UserRole> userRoles = userRoleMapper.selectList(Wrappers.<UserRole>lambdaQuery()
                .eq(UserRole::getUserId, userId)
                .in(CollectionUtils.isNotEmpty(param.getRoleIds()), UserRole::getRoleId, param.getRoleIds())
        );

        if (CollectionUtils.isEmpty(userRoles)) {
            return new ArrayList<>();
        }

        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        List<Role> roles = roleMapper.selectList(Wrappers.<Role>lambdaQuery()
                .eq(Role::getRealmId, realmId)
                .in(Role::getRoleId, roleIds)
                .eq(param.getClientId() != null, Role::getClientId, param.getClientId())
                .eq(StringUtils.isNotBlank(param.getName()), Role::getName, param.getName())
                .like(StringUtils.isNotBlank(param.getDimName()), Role::getName, param.getDimName())
                .in(CollectionUtils.isNotEmpty(param.getNames()), Role::getName, param.getNames())
                .eq(Role::getIsEnabled, true)

        );
        if (CollectionUtils.isNotEmpty(roles)) {
            return new ArrayList<>();
        }

        return EnhancedBeanUtils.createAndCopyList(roles, RoleVO.class);
    }

}
