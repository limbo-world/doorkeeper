package org.limbo.doorkeeper.core.domain.repository;

import org.limbo.doorkeeper.core.domain.entity.policy.Policy;

import java.util.List;

/**
 * @author yuansheng
 * @since 2022/2/4
 */
public interface PolicyRepository {

    List<Policy> listByIds(List<Long> ids);
}
