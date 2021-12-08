package org.limbo.doorkeeper.server.domain.policy;

import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.api.constants.PolicyType;

import java.util.Collection;

/**
 * @author Devil
 * @since 2021/11/30
 */
public abstract class PolicyEntity<T> {

    /**
     * 策略类型
     */
    protected PolicyType type;
    /**
     * 特定的才有 如组合 角色
     */
    protected Logic logic;
    /**
     * 执行逻辑
     */
    protected Intention intention;

    public Intention result(Collection<T> list) {
        boolean checkPassed = !CollectionUtils.isEmpty(list) && doResult(list);
        return reverseIntentionIfNotPassed(intention, checkPassed);
    }

    protected abstract boolean doResult(Collection<T> list);

    /**
     * 当策略检查结果为未通过时，将intention反转。<br/>
     * 即，当checkPassed为false时，入参intention是ALLOW就返回REFUSE，是REFUSE就返回ALLOW。<br/>
     * 当checkPassed为true时，返回入参intention。<br/>
     *
     * @param intention   检测的策略intention
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

}
