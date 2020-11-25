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

package org.limbo.doorkeeper.admin.controller;

import org.limbo.doorkeeper.api.client.PermissionClient;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.PermissionAddParam;
import org.limbo.doorkeeper.api.model.param.PermissionBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.PermissionQueryParam;
import org.limbo.doorkeeper.api.model.param.PermissionUpdateParam;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/20 10:41 AM
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionClient permissionClient;

    @PostMapping
    public Response<PermissionVO> add(@RequestBody PermissionAddParam param) {
        return permissionClient.add(param);
    }

    @PutMapping("/{permissionId}")
    public Response<Integer> update(@PathVariable("permissionId") Long permissionId, @RequestBody PermissionUpdateParam param) {
        return permissionClient.update(permissionId, param);
    }

    @PutMapping
    public Response<Boolean> batchUpdate(@RequestBody PermissionBatchUpdateParam param) {
        return permissionClient.batchUpdate(param);
    }

    @DeleteMapping
    public Response<Boolean> delete(@RequestBody List<Long> permissionIds) {
        return permissionClient.delete(permissionIds);
    }

    @GetMapping
    public Response<List<PermissionVO>> list() {
        return permissionClient.list();
    }

    @GetMapping("/query")
    public Response<Page<PermissionVO>> query(PermissionQueryParam param) {
        return permissionClient.query(param);
    }
}
