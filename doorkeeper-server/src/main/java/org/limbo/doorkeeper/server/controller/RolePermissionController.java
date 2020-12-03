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
import io.swagger.v3.oas.annotations.tags.Tag;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.RolePermissionAddParam;
import org.limbo.doorkeeper.api.model.param.RolePermissionQueryParam;
import org.limbo.doorkeeper.api.model.vo.RolePermissionVO;
import org.limbo.doorkeeper.server.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/23 10:31 AM
 */
@Tag(name = "角色权限绑定")
@RestController
@RequestMapping("/role-permission")
public class RolePermissionController extends BaseController {

    @Autowired
    private RolePermissionService rolePermissionService;

    @GetMapping
    @Operation(summary = "获取角色权限")
    public Response<List<RolePermissionVO>> list(RolePermissionQueryParam param) {
        return Response.ok(rolePermissionService.list(getParamProjectId(), param));
    }

    @PostMapping
    @Operation(summary = "绑定角色权限")
    public Response<Boolean> addRolePermission(@RequestBody List<RolePermissionAddParam> params) {
        rolePermissionService.addRolePermission(getParamProjectId(), params);
        return Response.ok(true);
    }


    @DeleteMapping
    @Operation(summary = "删除角色权限")
    public Response<Integer> deleteRolePermission(@RequestBody List<Long> rolePermissionIds){
        return Response.ok(rolePermissionService.deleteRolePermission(getParamProjectId(), rolePermissionIds));
    }

}
