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

package org.limbo.doorkeeper.server.adapter.http.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.dto.param.add.PermissionAddParam;
import org.limbo.doorkeeper.api.dto.param.query.PermissionQueryParam;
import org.limbo.doorkeeper.api.dto.param.batch.PermissionBatchUpdateParam;
import org.limbo.doorkeeper.api.dto.param.update.PermissionUpdateParam;
import org.limbo.doorkeeper.api.dto.vo.PageVO;
import org.limbo.doorkeeper.api.dto.vo.PermissionVO;
import org.limbo.doorkeeper.api.dto.vo.ResponseVO;
import org.limbo.doorkeeper.server.adapter.http.controller.BaseController;
import org.limbo.doorkeeper.server.application.service.PermissionService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @author Devil
 * @since 2021/1/5 4:46 下午
 */
@Tag(name = "权限")
@Slf4j
@RestController
@RequestMapping("/api/doorkeeper/v1/admin/realm/{realmId}/client/{clientId}/permission")
public class AdminPermissionController extends BaseController {

    @Autowired
    private PermissionService permissionService;

    @Operation(summary = "新建权限")
    @PostMapping
    public ResponseVO<PermissionVO> add(@RequestBody @Validated PermissionAddParam param) {
        return ResponseVO.success(permissionService.add(getRealmId(), getClientId(), param));
    }

    @Operation(summary = "批量修改权限")
    @PostMapping("/batch")
    public ResponseVO<Void> batch(@RequestBody @Validated PermissionBatchUpdateParam param) {
        permissionService.batchUpdate(getRealmId(), getClientId(), param);
        return ResponseVO.success();
    }

    @Operation(summary = "分页查询权限")
    @GetMapping
    public ResponseVO<PageVO<PermissionVO>> page(@ParameterObject PermissionQueryParam param) {
        return ResponseVO.success(permissionService.page(getRealmId(), getClientId(), param));
    }

    @Operation(summary = "查询权限")
    @GetMapping("/{permissionId}")
    public ResponseVO<PermissionVO> get(@Validated @NotNull(message = "未提交权限ID") @PathVariable("permissionId") Long permissionId) {
        return ResponseVO.success(permissionService.get(getRealmId(), getClientId(), permissionId));
    }

    @Operation(summary = "更新权限")
    @PutMapping("/{permissionId}")
    public ResponseVO<Void> update(@Validated @NotNull(message = "未提交权限ID") @PathVariable("permissionId") Long permissionId,
                                   @Validated @RequestBody PermissionUpdateParam param) {
        permissionService.update(getRealmId(), getClientId(), permissionId, param);
        return ResponseVO.success();
    }
}
