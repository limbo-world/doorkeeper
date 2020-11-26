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

package org.limbo.doorkeeper.api.client;

import org.limbo.doorkeeper.api.client.fallback.ApiClientFallback;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.ApiAddParam;
import org.limbo.doorkeeper.api.model.param.ApiQueryParam;
import org.limbo.doorkeeper.api.model.param.ApiUpdateParam;
import org.limbo.doorkeeper.api.model.vo.ApiVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/23 4:42 PM
 */
@FeignClient(name = "doorkeeper-server", path = "/api", contextId = "apiClient", fallbackFactory = ApiClientFallback.class)
public interface ApiClient {

    @GetMapping
    Response<List<ApiVO>> list();

    @GetMapping("/query")
    Response<Page<ApiVO>> page(@SpringQueryMap ApiQueryParam param);

    @PostMapping
    Response<ApiVO> add(@RequestBody ApiAddParam param);

    @PutMapping("/{apiId}")
    Response<Integer> update(@PathVariable("apiId") Long apiId, @RequestBody ApiUpdateParam param);

    @DeleteMapping
    Response<Boolean> batchDelete(@RequestBody List<Long> apiIds);
}
