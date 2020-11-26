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

import io.swagger.v3.oas.annotations.media.Schema;
import org.limbo.doorkeeper.api.client.fallback.RoleClinentFallback;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.RoleAddParam;
import org.limbo.doorkeeper.api.model.param.RoleQueryParam;
import org.limbo.doorkeeper.api.model.param.RoleUpdateParam;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuqingtong
 * @date 2020/11/20 17:50
 */
@FeignClient(name = "doorkeeper-server", path = "/role", contextId = "roleClient", fallbackFactory = RoleClinentFallback.class)
public interface RoleClient {

    @PostMapping
    Response<RoleVO> add(@RequestBody RoleAddParam param);

    @PutMapping("/{roleId}")
    Response<Integer> update(@PathVariable("roleId") Long roleId,
                             @RequestBody RoleUpdateParam param);

    @DeleteMapping
    Response<Integer> delete(@Schema(title = "角色ID") List<Long> roleIds);


    @GetMapping("/query")
    Response<Page<RoleVO>> page(@SpringQueryMap RoleQueryParam param);

    @GetMapping
    Response<List<RoleVO>> list();

}
