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
import org.limbo.doorkeeper.api.model.param.TagQueryParam;
import org.limbo.doorkeeper.api.model.vo.TagVO;
import org.limbo.doorkeeper.server.dal.entity.Tag;
import org.limbo.doorkeeper.server.dal.mapper.TagMapper;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Devil
 * @date 2021/4/19 8:26 下午
 */
@Service
public class TagService {

    @Autowired
    private TagMapper tagMapper;

    public List<TagVO> list(Long realmId, Long clientId, TagQueryParam param) {
        List<Tag> tags = tagMapper.selectList(Wrappers.<Tag>lambdaQuery()
                .eq(Tag::getRealmId, realmId)
                .eq(Tag::getClientId, clientId)
                .eq(StringUtils.isNotBlank(param.getK()), Tag::getK, param.getK())
                .like(StringUtils.isNotBlank(param.getDimK()), Tag::getK, param.getDimK())
                .eq(StringUtils.isNotBlank(param.getV()), Tag::getV, param.getV())
                .like(StringUtils.isNotBlank(param.getDimV()), Tag::getV, param.getDimV())
                .eq(StringUtils.isNotBlank(param.getKv()), Tag::getKv, param.getKv())
        );
        return EnhancedBeanUtils.createAndCopyList(tags, TagVO.class);
    }
}
