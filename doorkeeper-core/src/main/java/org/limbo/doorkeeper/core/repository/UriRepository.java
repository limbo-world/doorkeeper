package org.limbo.doorkeeper.core.repository;

import org.limbo.doorkeeper.core.domian.aggregate.Uri;

import java.util.List;

/**
 * @author yuansheng
 * @since 2022/2/2
 */
public interface UriRepository {

    List<Uri> listByRealmAndNamespace(Long realmId, Long namespaceId);

}
