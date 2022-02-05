package org.limbo.doorkeeper.core.domain.repository;

import org.limbo.doorkeeper.core.domain.dto.ResourceQueryDTO;

import java.util.List;

/**
 * @author yuansheng
 * @since 2022/2/3
 */
public interface ResourceRepository {

    List<Long> findResourceIds(ResourceQueryDTO resourceQueryDTO);

}
