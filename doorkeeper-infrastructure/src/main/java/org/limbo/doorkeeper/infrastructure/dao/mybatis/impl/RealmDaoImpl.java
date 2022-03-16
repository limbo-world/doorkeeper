package org.limbo.doorkeeper.infrastructure.dao.mybatis.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.infrastructure.dao.RealmDao;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.RealmMapper;
import org.limbo.doorkeeper.infrastructure.po.RealmPO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yuansheng
 * @since 2022/3/16
 */
@Component
public class RealmDaoImpl implements RealmDao {

    @Resource
    private RealmMapper realmMapper;

    @Override
    public RealmPO getById(Long realmId) {
        return realmMapper.selectById(realmId);
    }

    @Override
    public int updateById(Long realmId, String name, String secret) {
        return realmMapper.update(null, Wrappers.<RealmPO>lambdaUpdate()
                .set(StringUtils.isNoneBlank(name), RealmPO::getName, name)
                .set(StringUtils.isNoneBlank(secret), RealmPO::getSecret, secret)
                .eq(RealmPO::getRealmId, realmId)
        );
    }

    @Override
    public Long add(String name, String secret) {
        RealmPO realmPO = RealmPO.builder()
                .name(name)
                .secret(secret)
                .build();
        realmMapper.insert(realmPO);
        return realmPO.getRealmId();
    }

    @Override
    public List<RealmPO> getByIds(List<Long> realmIds) {
        return realmMapper.selectList(Wrappers.<RealmPO>lambdaQuery()
                .in(RealmPO::getRealmId, realmIds)
        );
    }

    @Override
    public RealmPO getDoorkeeperRealm() {
        return realmMapper.getDoorkeeperRealm();
    }


}
