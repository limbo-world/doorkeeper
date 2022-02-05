package org.limbo.doorkeeper.core.domain.repository;

import org.limbo.doorkeeper.core.domain.entity.Uri;

import java.util.List;

/**
 * @author yuansheng
 * @since 2022/2/2
 */
public interface LabelRepository {

    List<Uri> listByRealmAndNamespace(Long realmId, Long namespaceId);

}
