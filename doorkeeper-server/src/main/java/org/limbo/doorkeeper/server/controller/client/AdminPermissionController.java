/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   	http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.limbo.doorkeeper.server.controller.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.permission.PermissionAddParam;
import org.limbo.doorkeeper.api.model.param.permission.PermissionBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.permission.PermissionQueryParam;
import org.limbo.doorkeeper.api.model.param.permission.PermissionUpdateParam;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.limbo.doorkeeper.server.controller.BaseController;
import org.limbo.doorkeeper.server.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @author Devil
 * @date 2021/1/5 4:46 下午
 */
@Tag(name = "权限")
@Slf4j
@RestController
@RequestMapping("/admin/realm/{realmId}/client/{clientId}/permission")
public class AdminPermissionController extends BaseController {

    @Autowired
    private PermissionService permissionService;

    @Operation(summary = "新建权限")
    @PostMapping
    public Response<PermissionVO> add(@RequestBody @Validated PermissionAddParam param) {
        return Response.success(permissionService.add(getRealmId(), getClientId(), param));
    }

    @Operation(summary = "批量修改权限")
    @PostMapping("/batch")
    public Response<Void> batch(@RequestBody @Validated PermissionBatchUpdateParam param) {
        permissionService.batchUpdate(getRealmId(), getClientId(), param);
        return Response.success();
    }

    @Operation(summary = "分页查询权限")
    @GetMapping
    public Response<Page<PermissionVO>> page(@Validated PermissionQueryParam param) {
        return Response.success(permissionService.page(getRealmId(), getClientId(), param));
    }

    @Operation(summary = "查询权限")
    @GetMapping("/{permissionId}")
    public Response<PermissionVO> get(@Validated @NotNull(message = "未提交权限ID") @PathVariable("permissionId") Long permissionId) {
        return Response.success(permissionService.get(getRealmId(), getClientId(), permissionId));
    }

    @Operation(summary = "更新权限")
    @PutMapping("/{permissionId}")
    public Response<Void> update(@Validated @NotNull(message = "未提交权限ID") @PathVariable("permissionId") Long permissionId,
                                   @Validated @RequestBody PermissionUpdateParam param) {
        permissionService.update(getRealmId(), getClientId(), permissionId, param);
        return Response.success();
    }
}
