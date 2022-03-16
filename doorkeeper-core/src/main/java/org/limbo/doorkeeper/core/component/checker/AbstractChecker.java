package org.limbo.doorkeeper.core.component.checker;

import org.limbo.doorkeeper.core.domian.aggregate.User;

/**
 * @author yuansheng
 * @since 2022/2/3
 */
public abstract class AbstractChecker {

    protected User user;

    public abstract CheckResult check();

}
