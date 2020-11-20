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

package org.limbo.doorkeeper.server.service;

import org.limbo.doorkeeper.api.model.param.ApiAddParam;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/19 7:01 PM
 */
public interface ApiService {

    /**
     * 添加api
     */
    void addApi(List<ApiAddParam> apis);

    /**
     * 修改api
     */
    void updateApi(Long apiId, String describe);

    /**
     * 删除api
     */
    void deleteApi(Long apiId);
}