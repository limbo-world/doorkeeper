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

import org.limbo.doorkeeper.api.constants.PolicyType;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.dal.mapper.*;
import org.limbo.doorkeeper.server.service.UserRoleService;
import org.limbo.doorkeeper.server.support.auth.AuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author brozen
 * @date 2021/1/18
 */
@Component
public class PolicyCheckerFactory {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private GroupUserMapper groupUserMapper;

    @Autowired
    private GroupMapper groupMapper;

    /**
     * 根据策略类型创建策略检查器
     *
     * @param policy 策略
     * @return {@link PolicyChecker}
     */
    public PolicyChecker newPolicyChecker(PolicyVO policy) {
        if (policy == null) {
            throw new AuthorizationException("policy不可为null");
        }

        PolicyType policyType = PolicyType.parse(policy.getType());
        if (policyType == null) {
            throw new AuthorizationException("解析policy类型失败，类型为null，policy=" + policy);
        }

        switch (policyType) {
            case ROLE:
                return newRolePolicyChecker(policy);

            case PARAM:
                return newParamPolicyChecker(policy);

            case USER:
                return newUserPolicyChecker(policy);

            case GROUP:
                return newGroupPolicyChecker(policy);

            case COMBINE:
            case TIME:
            default:
                throw new AuthorizationException("不支持的策略类型:" + policyType);
        }
    }

    /**
     * 创建根据 用户 检查的策略checker
     */
    public PolicyChecker newUserPolicyChecker(PolicyVO policy) {
        UserPolicyChecker userPolicyChecker = new UserPolicyChecker(policy);
        userPolicyChecker.setUserMapper(userMapper);
        return userPolicyChecker;
    }

    /**
     * 创建根据 用户组 检查的策略checker
     */
    public PolicyChecker newGroupPolicyChecker(PolicyVO policy) {
        GroupPolicyChecker userPolicyChecker = new GroupPolicyChecker(policy);
        userPolicyChecker.setGroupUserMapper(groupUserMapper);
        userPolicyChecker.setGroupMapper(groupMapper);
        return userPolicyChecker;
    }

    /**
     * 创建根据 请求参数 检查的策略checker
     */
    public PolicyChecker newParamPolicyChecker(PolicyVO policy) {
        return new ParamPolicyChecker(policy);
    }

    /**
     * 创建根据 用户角色 检查的策略checker
     */
    public PolicyChecker newRolePolicyChecker(PolicyVO policy) {
        RolePolicyChecker rolePolicyChecker = new RolePolicyChecker(policy);
        rolePolicyChecker.setUserRoleService(userRoleService);
        return rolePolicyChecker;
    }

}
