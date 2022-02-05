package org.limbo.doorkeeper.core.domain.repository;

import org.limbo.doorkeeper.core.domain.constants.IDRelationEnum;
import org.limbo.doorkeeper.core.domain.entity.IDRelation;

import java.util.List;

/**
 * @author yuansheng
 * @since 2022/2/4
 */
public interface IDRelationRepository {

    IDRelation find(Long id, IDRelationEnum relation);

    List<IDRelation> list(List<Long> ids, IDRelationEnum relation);

}
