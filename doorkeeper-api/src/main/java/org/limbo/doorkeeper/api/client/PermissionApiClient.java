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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.limbo.doorkeeper.api.client.fallback.PermissionApiClientFallback;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.PermissionApiAddParam;
import org.limbo.doorkeeper.api.model.param.PermissionApiQueryParam;
import org.limbo.doorkeeper.api.model.vo.PermissionApiVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author liuqingtong
 * @date 2020/11/20 11:59
 */
@FeignClient(name = "doorkeeper-server", path = "/permission-api", contextId = "permissionApiClient", fallbackFactory = PermissionApiClientFallback.class)
public interface PermissionApiClient {

    @GetMapping
    Response<List<PermissionApiVO>> list(PermissionApiQueryParam param);

    @PostMapping
    Response<Boolean> addPermissionApi(@RequestBody List<PermissionApiAddParam> permissionApis);


    @DeleteMapping
    Response<Integer> deletePermissionApi(@RequestBody List<Long> permissionApiIds);


}
