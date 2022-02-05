package org.limbo.doorkeeper.core.domain.repository;

import org.limbo.doorkeeper.core.domain.entity.Permission;

import java.util.List;

/**
 * @author yuansheng
 * @since 2022/2/4
 */
public interface PermissionRepository {

    List<Permission> list(List<Long> ids);
}
