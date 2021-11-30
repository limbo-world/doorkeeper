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
import org.limbo.doorkeeper.api.dto.param.query.UriQueryParam;
import org.limbo.doorkeeper.api.dto.vo.UriVO;
import org.limbo.doorkeeper.infrastructure.po.UriPO;
import org.limbo.doorkeeper.infrastructure.mapper.UriMapper;
import org.limbo.doorkeeper.server.infrastructure.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Devil
 * @since 2021/4/19 8:26 下午
 */
@Service
public class UriService {

    @Autowired
    private UriMapper uriMapper;

    public List<UriVO> list(Long realmId, Long clientId, UriQueryParam param) {
        List<UriPO> uris = uriMapper.selectList(Wrappers.<UriPO>lambdaQuery()
                .eq(UriPO::getRealmId, realmId)
                .eq(UriPO::getClientId, clientId)
                .eq(param.getMethod() != null, UriPO::getMethod, param.getMethod())
                .eq(StringUtils.isNotBlank(param.getUri()), UriPO::getUri, param.getUri())
                .like(StringUtils.isNotBlank(param.getDimUri()), UriPO::getUri, param.getDimUri())
        );
        return EnhancedBeanUtils.createAndCopyList(uris, UriVO.class);
    }
}
