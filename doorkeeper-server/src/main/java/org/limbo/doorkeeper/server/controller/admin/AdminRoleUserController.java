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

package org.limbo.doorkeeper.server.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.role.RoleUserBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.role.RoleUserQueryParam;
import org.limbo.doorkeeper.api.model.vo.RoleUserVO;
import org.limbo.doorkeeper.server.controller.BaseController;
import org.limbo.doorkeeper.server.service.RoleUserService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * 如果是域角色 clientId 为 0
 * @author Devil
 * @date 2021/1/4 5:19 下午
 */
@Tag(name = "角色用户")
@Slf4j
@RestController
@RequestMapping("/api/admin/realm/{realmId}/role")
public class AdminRoleUserController extends BaseController {

    @Autowired
    private RoleUserService roleUserService;

    @Operation(summary = "查询角色用户列表")
    @GetMapping("/{roleId}/user")
    public Response<Page<RoleUserVO>> page(@Validated @NotNull(message = "未提交角色ID") @PathVariable("roleId") Long roleId,
                                           @ParameterObject RoleUserQueryParam param) {
        return Response.success(roleUserService.page(getRealmId(), roleId, param));
    }

    @Operation(summary = "批量修改角色用户")
    @PostMapping("/{roleId}/role-user/batch")
    public Response<Void> batch(@Validated @NotNull(message = "未提交角色ID") @PathVariable("roleId") Long roleId,
                                @RequestBody @Validated RoleUserBatchUpdateParam param) {
        roleUserService.batchUpdate(roleId, param);
        return Response.success();
    }

}
