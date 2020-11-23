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
import org.limbo.doorkeeper.api.client.fallback.PermissionClientFallback;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.PermissionAddParam;
import org.limbo.doorkeeper.api.model.param.PermissionBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.PermissionUpdateParam;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/23 4:42 PM
 */
@FeignClient(name = "doorkeeper-server", path = "/permission", contextId = "permissionClient", fallbackFactory = PermissionClientFallback.class)
public interface PermissionClient {

    @PostMapping
    Response<PermissionVO> add(@RequestBody PermissionAddParam param);

    @PutMapping("/{permissionId}")
    Response<Integer> update(@PathVariable("permissionId") Long permissionId, @RequestBody PermissionUpdateParam param);

    @PutMapping
    Response<Boolean> batchUpdate(@RequestBody PermissionBatchUpdateParam param);

    @DeleteMapping
    Response<Boolean> delete(@RequestBody List<Long> permissionIds);

    @GetMapping
    Response<List<PermissionVO>> list();
}
