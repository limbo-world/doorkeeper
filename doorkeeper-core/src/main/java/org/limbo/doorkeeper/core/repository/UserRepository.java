package org.limbo.doorkeeper.core.repository;

import org.limbo.doorkeeper.core.domian.aggregate.User;

/**
 * @author yuansheng
 * @since 2022/2/7
 */
public interface UserRepository {

    Long save(User user);

    User getById(Long userId);

}
