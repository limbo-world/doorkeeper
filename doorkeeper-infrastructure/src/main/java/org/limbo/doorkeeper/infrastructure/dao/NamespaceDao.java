package org.limbo.doorkeeper.infrastructure.dao;

import org.limbo.doorkeeper.api.dto.param.update.ClientUpdateParam;
import org.limbo.doorkeeper.infrastructure.po.NamespacePO;

import java.util.List;

/**
 * @author yuansheng
 * @since 2022/3/16
 */
public interface NamespaceDao {

    List<NamespacePO> listByRealm(Long realmId);

    NamespacePO getById(Long namespaceId);

    void updateById(NamespacePO namespacePO);
}
