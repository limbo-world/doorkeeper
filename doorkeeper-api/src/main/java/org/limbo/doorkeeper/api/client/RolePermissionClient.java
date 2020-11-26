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

import org.limbo.doorkeeper.api.client.fallback.RolePermissionClientFallback;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.RolePermissionAddParam;
import org.limbo.doorkeeper.api.model.param.RolePermissionQueryParam;
import org.limbo.doorkeeper.api.model.vo.RolePermissionVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/23 4:43 PM
 */
@FeignClient(name = "doorkeeper-server", path = "/role-permission", contextId = "rolePermissionClient", fallbackFactory = RolePermissionClientFallback.class)
public interface RolePermissionClient {

    @GetMapping
    Response<List<RolePermissionVO>> list(@SpringQueryMap RolePermissionQueryParam param);

    @PostMapping
    Response<Boolean> addRolePermission(@RequestBody List<RolePermissionAddParam> params);


    @DeleteMapping
    Response<Integer> deleteRolePermission(@RequestBody List<Long> rolePermissionIds);
}
