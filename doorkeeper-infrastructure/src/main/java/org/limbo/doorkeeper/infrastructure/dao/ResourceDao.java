package org.limbo.doorkeeper.infrastructure.dao;

import org.limbo.doorkeeper.infrastructure.dto.PermissionResourceDTO;
import org.limbo.doorkeeper.infrastructure.dto.ResourceQueryCommand;

import java.util.List;

/**
 * @author yuansheng
 * @since 2022/2/5
 */
public interface ResourceDao {

    List<Long> findResourceIds(ResourceQueryCommand resourceQueryCommand);

    List<PermissionResourceDTO> listPermissionIdsByResourceIds(List<Long> resourceIds);
}
