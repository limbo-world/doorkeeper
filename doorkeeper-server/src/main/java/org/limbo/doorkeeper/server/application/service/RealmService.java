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

package org.limbo.doorkeeper.server.application.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.dto.param.update.RealmUpdateParam;
import org.limbo.doorkeeper.api.dto.vo.RealmVO;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.RealmMapper;
import org.limbo.doorkeeper.server.infrastructure.po.RealmPO;
import org.limbo.utils.reflection.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Devil
 * @since 2021/1/11 11:17 上午
 */
@Service
public class RealmService {

    @Autowired
    private RealmMapper realmMapper;

    public RealmVO get(Long realmId) {
        RealmPO realm = realmMapper.selectById(realmId);
        return EnhancedBeanUtils.createAndCopy(realm, RealmVO.class);
    }

    public void update(Long realmId, RealmUpdateParam param) {
        realmMapper.update(null, Wrappers.<RealmPO>lambdaUpdate()
                .set(StringUtils.isNotBlank(param.getSecret()), RealmPO::getSecret, param.getSecret())
                .eq(RealmPO::getRealmId, realmId)
        );
    }

}
