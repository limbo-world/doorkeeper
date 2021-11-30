/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   	http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.limbo.doorkeeper.server.service;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.limbo.doorkeeper.api.dto.param.query.TagQueryParam;
import org.limbo.doorkeeper.api.dto.vo.TagVO;
import org.limbo.doorkeeper.infrastructure.po.TagPO;
import org.limbo.doorkeeper.infrastructure.mapper.TagMapper;
import org.limbo.doorkeeper.server.infrastructure.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Devil
 * @since 2021/4/19 8:26 下午
 */
@Service
public class TagService {

    @Autowired
    private TagMapper tagMapper;

    public List<TagVO> list(Long realmId, Long clientId, TagQueryParam param) {
        List<TagPO> tags = tagMapper.selectList(Wrappers.<TagPO>lambdaQuery()
                .eq(TagPO::getRealmId, realmId)
                .eq(TagPO::getClientId, clientId)
                .eq(StringUtils.isNotBlank(param.getK()), TagPO::getK, param.getK())
                .like(StringUtils.isNotBlank(param.getDimK()), TagPO::getK, param.getDimK())
                .eq(StringUtils.isNotBlank(param.getV()), TagPO::getV, param.getV())
                .like(StringUtils.isNotBlank(param.getDimV()), TagPO::getV, param.getDimV())
                .eq(StringUtils.isNotBlank(param.getKv()), TagPO::getKv, param.getKv())
        );
        return EnhancedBeanUtils.createAndCopyList(tags, TagVO.class);
    }
}
