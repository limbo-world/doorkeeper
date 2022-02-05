package org.limbo.doorkeeper.core.domain.entity.policy;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author Devil
 * @since 2021/11/30
 */
public class UserPolicy extends Policy {

    private final List<Long> users;

    public UserPolicy(List<Long> users) {
        this.users = users;
    }

    @Override
    protected boolean doResult(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return false;
        }
        return users.contains(userIds.get(0));
    }
}
