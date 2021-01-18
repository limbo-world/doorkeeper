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

import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.support.auth2.params.AuthorizationCheckParam;

/**
 * @author brozen
 * @date 2021/1/18
 */
public abstract class AbstractPolicyChecker implements PolicyChecker {

    /**
     * 待检查的策略
     */
    protected PolicyVO policy;

    public AbstractPolicyChecker(PolicyVO policy) {
        this.policy = policy;
    }

    /**
     * {@inheritDoc}
     *
     * @param authorizationCheckParam 授权校验参数
     * @return
     */
    @Override
    public Intention check(AuthorizationCheckParam<?> authorizationCheckParam) {
        Intention intention = Intention.parse(policy.getIntention());
        return reverseIntentionIfNotPassed(intention, doCheck(authorizationCheckParam));
    }

    /**
     * 检测策略是否通过
     * @param authorizationCheckParam 授权校验参数
     */
    protected abstract boolean doCheck(AuthorizationCheckParam<?> authorizationCheckParam);

    /**
     * 当策略检查结果为未通过时，将intention反转。<br/>
     * 即，当checkPassed为false时，入参intention是ALLOW就返回REFUSE，是REFUSE就返回ALLOW。<br/>
     * 当checkPassed为true时，返回入参intention。<br/>
     * @param intention 检测的策略intention
     * @param checkPassed 策略检测是否通过
     */
    protected Intention reverseIntentionIfNotPassed(Intention intention, boolean checkPassed) {
        if (intention == Intention.ALLOW) {
            return checkPassed ? Intention.ALLOW : Intention.REFUSE;
        } else if (intention == Intention.REFUSE) {
            return checkPassed ? Intention.REFUSE : Intention.ALLOW;
        } else {
            throw new IllegalArgumentException("intention 为null");
        }
    }

    /**
     * 解析并返回policy的逻辑，解析失败抛出{@link IllegalArgumentException}
     * @return {@link Logic}
     */
    protected Logic getPolicyLogic() {
        if (policy == null) {
            throw new IllegalArgumentException("policy为null");
        }

        Logic logic = Logic.parse(policy.getLogic());
        if (logic == null) {
            throw new IllegalArgumentException("无法解析的逻辑，policy=" + policy);
        }
        return logic;
    }

}
