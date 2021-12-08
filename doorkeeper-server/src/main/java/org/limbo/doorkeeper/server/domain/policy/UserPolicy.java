package org.limbo.doorkeeper.server.domain.policy;

import org.apache.commons.lang3.BooleanUtils;
import org.limbo.doorkeeper.api.dto.vo.policy.PolicyUserVO;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author Devil
 * @since 2021/11/30
 */
public class UserPolicy extends PolicyEntity<UserValueObject> {

    private List<PolicyUserVO> users;

    public UserPolicy(List<PolicyUserVO> users) {
        this.users = users;
    }

    @Override
    protected boolean doResult(Collection<UserValueObject> list) {
        UserValueObject user = list.iterator().next();
        return user != null && BooleanUtils.isTrue(user.getIsEnabled()) && users.stream()
                .map(PolicyUserVO::getUserId)
                .anyMatch(uid -> Objects.equals(uid, user.getUserId()));
    }
}
