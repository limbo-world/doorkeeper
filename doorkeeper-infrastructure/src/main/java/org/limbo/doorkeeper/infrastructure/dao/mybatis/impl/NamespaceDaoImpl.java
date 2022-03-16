package org.limbo.doorkeeper.infrastructure.dao.mybatis.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.infrastructure.dao.NamespaceDao;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.NamespaceMapper;
import org.limbo.doorkeeper.infrastructure.po.NamespacePO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yuansheng
 * @since 2022/3/16
 */
@Component
public class NamespaceDaoImpl implements NamespaceDao {

    @Resource
    private NamespaceMapper namespaceMapper;

    @Override
    public List<NamespacePO> listByRealm(Long realmId) {
        return namespaceMapper.selectList(Wrappers.<NamespacePO>lambdaQuery()
                .eq(NamespacePO::getRealmId, realmId)
                .orderByDesc(NamespacePO::getNamespaceId)
        );
    }

    @Override
    public NamespacePO getById(Long namespaceId) {
        return namespaceMapper.selectById(namespaceId);
    }

    @Override
    public void updateById(NamespacePO namespacePO) {
        namespaceMapper.updateById(namespacePO);
    }
}
