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
import org.limbo.doorkeeper.api.model.param.PermissionApiAddParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author liuqingtong
 * @date 2020/11/20 11:59
 */
@Tag(name = "权限api")
@FeignClient(name = "doorkeeper-server", path = "/permission-api", contextId = "permissionApiClient")
interface PermissionApiClient {

    @PostMapping
    @Operation(summary = "绑定权限api")
    void addPermissionApi(@RequestBody List<PermissionApiAddParam> permissionApis);


    @DeleteMapping
    @Operation(summary = "删除权限api")
    void deletePermissionApi(@RequestBody List<Long> permissionApiIds);


}
