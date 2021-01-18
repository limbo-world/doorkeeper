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

package org.limbo.doorkeeper.server.support.auth2.policies;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyRoleVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.dao.GroupRoleMapper;
import org.limbo.doorkeeper.server.dao.GroupUserMapper;
import org.limbo.doorkeeper.server.dao.UserRoleMapper;
import org.limbo.doorkeeper.server.entity.GroupRole;
import org.limbo.doorkeeper.server.entity.GroupUser;
import org.limbo.doorkeeper.server.entity.UserRole;
import org.limbo.doorkeeper.server.support.auth2.params.AuthorizationCheckParam;

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
            List<Long> groupIds = groupUsers.stream().map(GroupUser::getGroupId).collect(Collectors.toList());
            // FIXME 这里考虑搞到mapper里去，可以简化为一个sql执行完，这样写代码有些臃肿
            List<Object> groupRoleIdObjs = groupRoleMapper.selectObjs(Wrappers.<GroupRole>lambdaQuery()
                    .select(GroupRole::getRoleId)
                    .in(GroupRole::getGroupId, groupIds)
                    .in(GroupRole::getRoleId, roleIds)
            );
            Set<Long> groupRoleIds = groupRoleIdObjs.stream()
                    .filter(obj -> obj instanceof Number)
                    .map(obj -> ((Number) obj).longValue())
                    .collect(Collectors.toSet());
            // FIXME 以上
            if (CollectionUtils.isNotEmpty(groupRoleIds)) {
                userRoleIds.addAll(groupRoleIds);
            }
        }


        // 解析策略逻辑，判断是否满足逻辑条件
        return getPolicyLogic().isSatisfied(roleIds.size(), userRoleIds.size());
    }
}
