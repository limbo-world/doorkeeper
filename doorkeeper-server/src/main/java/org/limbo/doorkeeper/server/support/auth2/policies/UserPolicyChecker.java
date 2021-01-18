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

import lombok.Setter;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyUserVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.dao.UserMapper;
import org.limbo.doorkeeper.server.support.auth2.params.AuthorizationCheckParam;

import java.util.Objects;

/**
 * @author brozen
 * @date 2021/1/18
 */
public class UserPolicyChecker extends AbstractPolicyChecker {

    @Setter
    private UserMapper userMapper;

    public UserPolicyChecker(PolicyVO policy) {
        super(policy);
    }


    /**
     * {@inheritDoc}<br/>
     *
     * 检查授权校验参数中的userId是否在策略授予的用户中
     *
     * @param authorizationCheckParam 授权校验参数
     * @return
     */
    @Override
    protected boolean doCheck(AuthorizationCheckParam<?> authorizationCheckParam) {
        Long userId = authorizationCheckParam.getUserId();

        return policy.getUsers().stream()
                .map(PolicyUserVO::getUserId)
                .anyMatch(uid -> Objects.equals(uid, userId));
    }

}
