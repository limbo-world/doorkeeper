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
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.RoleAddParam;
import org.limbo.doorkeeper.api.model.param.RoleQueryParam;
import org.limbo.doorkeeper.api.model.param.RoleUpdateParam;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.limbo.doorkeeper.server.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Devil
 * @date 2020/11/20 4:25 PM
 */
@Tag(name = "角色")
@RestController
@RequestMapping("/role")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    @Operation(summary = "新增角色")
    public Response<RoleVO> add(@Validated @RequestBody RoleAddParam param) {
        return Response.ok(roleService.addRole(getProjectId(), param));
    }

    @PutMapping("/{roleId}")
    @Operation(summary = "修改角色")
    public Response<Integer> update(@Validated @NotNull(message = "角色不存在") @PathVariable("roleId") Long roleId,
                                   @RequestBody RoleUpdateParam param) {
        param.setRoleId(roleId);
        return Response.ok(roleService.updateRole(getProjectId(), param));
    }

    @DeleteMapping
    @Operation(summary = "删除角色")
    public Response<Integer> delete(@Validated @NotEmpty(message = "角色不存在") @Schema(title = "角色ID") @RequestBody List<Long> roleIds) {
        return Response.ok(roleService.deleteRole(getProjectId(), roleIds));
    }

    @GetMapping
    @Operation(summary = "查询所有角色")
    public Response<List<RoleVO>> list() {
        return Response.ok(roleService.list(getProjectId()));
    }

    @GetMapping("/manager")
    @Operation(summary = "查询所有管理端角色")
    public Response<List<RoleVO>> managerRoles() {
        return Response.ok(roleService.managerRoles());
    }

    @GetMapping("/query")
    @Operation(summary = "分页查询角色")
    public Response<Page<RoleVO>> page(@Validated RoleQueryParam param) {
        return Response.ok(roleService.queryRole(getProjectId(), param));
    }

}
