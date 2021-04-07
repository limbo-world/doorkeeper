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
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.model.param.check.AuthorizationCheckParam;
import org.limbo.doorkeeper.api.model.param.check.RoleCheckParam;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyRoleVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.dal.mapper.GroupMapper;
import org.limbo.doorkeeper.server.dal.mapper.GroupRoleMapper;
import org.limbo.doorkeeper.server.dal.mapper.GroupUserMapper;
import org.limbo.doorkeeper.server.dal.mapper.RoleMapper;
import org.limbo.doorkeeper.server.service.UserRoleService;
import org.limbo.doorkeeper.server.support.auth.LogicChecker;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author brozen
 * @date 2021/1/18
 */
public class RolePolicyChecker extends AbstractPolicyChecker {

    @Setter
    private UserRoleService userRoleService;

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
    protected boolean doCheck(AuthorizationCheckParam authorizationCheckParam) {
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
        List<RoleVO> roles = userRoleService.checkRole(authorizationCheckParam.getUserId(), policy.getRealmId(), roleCheckParam);

        // 解析策略逻辑，判断是否满足逻辑条件
        return LogicChecker.isSatisfied(getPolicyLogic(), roleIds.size(), roles == null ? 0 : roles.size());
    }

}
