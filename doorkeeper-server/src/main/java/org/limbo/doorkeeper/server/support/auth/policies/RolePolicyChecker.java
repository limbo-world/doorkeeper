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

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.model.param.check.ResourceCheckParam;
import org.limbo.doorkeeper.api.model.param.check.RoleCheckParam;
import org.limbo.doorkeeper.api.model.vo.check.RoleCheckResult;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyRoleVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.dal.entity.User;
import org.limbo.doorkeeper.server.support.auth.LogicChecker;
import org.limbo.doorkeeper.server.support.auth.RoleChecker;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author brozen
 * @date 2021/1/18
 */
@Slf4j
public class RolePolicyChecker extends AbstractPolicyChecker {

    @Setter
    private RoleChecker roleChecker;

    public RolePolicyChecker(User user, PolicyVO policy) {
        super(user, policy);
    }

    /**
     * 获取用户角色  以及用户所在用户组角色（目前用户组角色是继承的，后面可以做成可配置的方式）
     *
     * @param resourceCheckParam 授权校验参数
     * @return
     */
    @Override
    protected boolean doCheck(ResourceCheckParam resourceCheckParam) {
        // 策略绑定的角色 去除未启用的
        List<Long> roleIds = policy.getRoles().stream()
                .filter(PolicyRoleVO::getIsEnabled)
                .map(PolicyRoleVO::getRoleId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(roleIds)) {
            return false;
        }

        RoleCheckParam roleCheckParam = new RoleCheckParam();
        roleCheckParam.setRoleIds(roleIds);
        RoleCheckResult check = roleChecker.check(user.getUserId(), roleCheckParam);

        // 解析策略逻辑，判断是否满足逻辑条件
        return LogicChecker.isSatisfied(getPolicyLogic(), roleIds.size(),
                check != null && check.getRoles() != null ? check.getRoles().size() : 0);
    }

}
