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
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.role.*;
import org.limbo.doorkeeper.api.model.vo.RoleCombineVO;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.limbo.doorkeeper.server.controller.BaseController;
import org.limbo.doorkeeper.server.service.RoleCombineService;
import org.limbo.doorkeeper.server.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/4 5:19 下午
 */
@Tag(name = "角色")
@Slf4j
@RestController
@RequestMapping("/admin/realm/{realmId}/client/{clientId}/role")
public class AdminClientRoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleCombineService roleCombineService;

    @Operation(summary = "新建角色")
    @PostMapping
    public Response<RoleVO> add(@RequestBody @Validated RoleAddParam param) {
        return Response.success(roleService.add(getRealmId(), getClientId(), param));
    }

    @Operation(summary = "查询角色列表")
    @GetMapping
    public Response<List<RoleVO>> list(@Validated RoleQueryParam param) {
        return Response.success(roleService.list(getRealmId(), getClientId(), param));
    }

    @Operation(summary = "查询角色")
    @GetMapping("/{roleId}")
    public Response<RoleVO> get(@Validated @NotNull(message = "未提交角色ID") @PathVariable("roleId") Long roleId) {
        return Response.success(roleService.get(getRealmId(), getClientId(), roleId));
    }

    @Operation(summary = "更新角色")
    @PutMapping("/{roleId}")
    public Response<RoleVO> update(@Validated @NotNull(message = "未提交角色ID") @PathVariable("roleId") Long roleId,
                                   @Validated @RequestBody RoleUpdateParam param) {
        roleService.update(getRealmId(), getClientId(), roleId, param);
        return Response.success();
    }

    @Operation(summary = "查询角色组合列表")
    @GetMapping("/{roleId}/role-combine")
    public Response<List<RoleCombineVO>> list(@Validated @NotNull(message = "未提交角色ID") @PathVariable("roleId") Long roleId,
                                              @Validated RoleCombineQueryParam param) {
        return Response.success(roleCombineService.list(getRealmId(), getClientId(), roleId, param));
    }

    @Operation(summary = "批量修改角色组合")
    @PostMapping("/{roleId}/role-combine/batch")
    public Response<List<RoleCombineVO>> batch(@Validated @NotNull(message = "未提交角色ID") @PathVariable("roleId") Long roleId,
                                               @RequestBody @Validated RoleCombineBatchUpdateParam param) {
        roleCombineService.batchUpdate(getRealmId(), getClientId(), roleId, param);
        return Response.success();
    }

}
