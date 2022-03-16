package org.limbo.doorkeeper.core.repository;

import org.limbo.doorkeeper.core.domian.aggregate.Permission;

import java.util.List;

/**
 * @author yuansheng
 * @since 2022/2/4
 */
public interface PermissionRepository {

    List<Permission> list(List<Long> ids);
}
