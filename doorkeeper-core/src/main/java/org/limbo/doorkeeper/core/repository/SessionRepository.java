package org.limbo.doorkeeper.core.repository;

import org.limbo.doorkeeper.core.domian.aggregate.Session;

/**
 * @author yuansheng
 * @since 2022/2/6
 */
public interface SessionRepository {

    Session save(Session sessionAggregate);

    Session get(String sessionId);

    Session delete(String sessionId);

    Session update(Session sessionAggregate);

}
