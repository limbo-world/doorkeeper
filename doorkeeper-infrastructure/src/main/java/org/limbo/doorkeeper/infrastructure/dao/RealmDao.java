package org.limbo.doorkeeper.infrastructure.dao;

import org.limbo.doorkeeper.infrastructure.po.RealmPO;

import java.util.List;

/**
 * @author yuansheng
 * @since 2022/2/6
 */
public interface RealmDao {

    RealmPO getById(Long realmId);

    int updateById(Long realmId, String name, String secret);

    /**
     * 新增
     * @param name
     * @param secret
     * @return realmId
     */
    Long add(String name, String secret);

    List<RealmPO> getByIds(List<Long> realmIds);

    RealmPO getDoorkeeperRealm();

}
