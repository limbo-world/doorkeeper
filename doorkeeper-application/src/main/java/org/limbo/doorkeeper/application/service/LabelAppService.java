package org.limbo.doorkeeper.application.service;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.limbo.doorkeeper.api.dto.param.query.LabelQueryParam;
import org.limbo.doorkeeper.api.dto.vo.TagVO;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.TagMapper;
import org.limbo.utils.reflection.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yuansheng
 * @since 2022/2/6
 */
@Service
public class LabelAppService {

    @Autowired
    private TagMapper tagMapper;

    public List<TagVO> list(Long realmId, Long clientId, LabelQueryParam param) {
        List<TagPO> tags = tagMapper.selectList(Wrappers.<TagPO>lambdaQuery()
                .eq(TagPO::getRealmId, realmId)
                .eq(TagPO::getNamespaceId, clientId)
                .eq(StringUtils.isNotBlank(param.getK()), TagPO::getKey, param.getK())
                .like(StringUtils.isNotBlank(param.getDimK()), TagPO::getKey, param.getDimK())
                .eq(StringUtils.isNotBlank(param.getV()), TagPO::getValue, param.getV())
                .like(StringUtils.isNotBlank(param.getDimV()), TagPO::getValue, param.getDimV())
                .eq(StringUtils.isNotBlank(param.getKv()), TagPO::getKeyValue, param.getKv())
        );
        return EnhancedBeanUtils.createAndCopyList(tags, TagVO.class);
    }

}
