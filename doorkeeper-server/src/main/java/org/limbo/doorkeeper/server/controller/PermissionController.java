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

package org.limbo.doorkeeper.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.PermissionAddParam;
import org.limbo.doorkeeper.api.model.param.PermissionBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.PermissionUpdateParam;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.limbo.doorkeeper.server.entity.Permission;
import org.limbo.doorkeeper.server.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Devil
 * @date 2020/11/20 10:41 AM
 */
@Tag(name = "权限")
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping
    @Operation(summary = "新增权限")
    public Response<PermissionVO> add(@RequestBody PermissionAddParam param) {
        return Response.ok(permissionService.addPermission(param));
    }

    @PutMapping("/{permissionId}")
    @Operation(summary = "修改权限")
    public Response<Integer> update(@Validated @NotNull(message = "权限不存在") @PathVariable("permissionId") Long permissionId,
                                    @RequestBody PermissionUpdateParam param) {
        param.setPermissionId(permissionId);
        return Response.ok(permissionService.updatePermission(param));
    }

    @PutMapping
    @Operation(summary = "批量修改权限")
    public Response<Boolean> batchUpdate(@RequestBody PermissionBatchUpdateParam param) {
        permissionService.batchUpdate(param);
        return Response.ok(true);
    }

    @DeleteMapping
    @Operation(summary = "批量删除权限")
    public Response<Boolean> delete(@RequestBody @Schema(title = "权限id列表", required = true) List<Long> permissionIds) {
        permissionService.deletePermission(permissionIds);
        return Response.ok(true);
    }

    @GetMapping
    @Operation(summary = "权限列表")
    public Response<List<PermissionVO>> list() {
        return Response.ok(permissionService.all());
    }
}
