/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.limbo.doorkeeper.server.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.limbo.doorkeeper.api.model.param.ApiAddParam;
import org.limbo.doorkeeper.server.dao.ApiMapper;
import org.limbo.doorkeeper.server.entity.Api;
import org.limbo.doorkeeper.server.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/19 7:17 PM
 */
@Service
public class ApiServiceImpl implements ApiService {

    @Autowired
    private ApiMapper apiMapper;

    @Override
    public void addApi(List<ApiAddParam> apis) {
        apiMapper.batchInsert(apis);
    }

    @Override
    public void updateApi(Long apiId, String describe) {
        apiMapper.update(null, Wrappers.<Api>lambdaUpdate()
                .set(Api::getApiDescribe, describe)
                .eq(Api::getApiId, apiId)
        );
    }

    @Override
    @Transactional
    public void deleteApi(Long apiId) {
        // 删除api
        apiMapper.update(null, Wrappers.<Api>lambdaUpdate()
                .set(Api::getIsDeleted, true)
                .eq(Api::getApiId, apiId)
        );
    }
}