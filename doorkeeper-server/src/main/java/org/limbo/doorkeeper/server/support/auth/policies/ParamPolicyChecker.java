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

import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyParamVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.api.model.param.auth.AuthorizationCheckParam;
import org.limbo.doorkeeper.server.support.auth.checker.LogicChecker;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author brozen
 * @date 2021/1/18
 */
public class ParamPolicyChecker extends AbstractPolicyChecker {

    public ParamPolicyChecker(PolicyVO policy) {
        super(policy);
    }

    /**
     * {@inheritDoc}<br/>
     *
     * 将 校验参数 和 策略限制的参数 进行对比，匹配的参数个数再根据{@link Logic}的约束进行判断，是否满足条件。
     *
     * @param authorizationCheckParam 授权校验参数
     * @return
     */
    @Override
    protected boolean doCheck(AuthorizationCheckParam<?> authorizationCheckParam) {
        Map<String, String> params = authorizationCheckParam.getParams();
        List<PolicyParamVO> policyParams = policy.getParams();
        Map<String, String> policyParamMap = policyParams.stream()
                .collect(Collectors.toMap(
                        PolicyParamVO::getK, PolicyParamVO::getV
                ));

        // 统计满足限制条件的参数个数
        int satisfiedParamCount = 0;
        for (Map.Entry<String, String> policyParamEntry : policyParamMap.entrySet()) {
            String v = params.get(policyParamEntry.getKey());
            if (v != null && v.equals(policyParamEntry.getValue())) {
                satisfiedParamCount++;
            }
        }

        return LogicChecker.isSatisfied(getPolicyLogic(), policyParamMap.size(), satisfiedParamCount);
    }
}
