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

package org.limbo.doorkeeper.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.AccountAdminRoleAddParam;
import org.limbo.doorkeeper.api.model.param.AccountAdminRoleQueryParam;
import org.limbo.doorkeeper.api.model.vo.AccountAdminRoleVO;
import org.limbo.doorkeeper.server.service.AccountAdminRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Devil
 * @Date 2020/12/7 10:00 上午
 */
@Tag(name = "管理端角色")
@RestController
@RequestMapping("/account-admin-role")
public class AccountAdminRoleController extends BaseController {

    @Autowired
    private AccountAdminRoleService accountAdminRoleService;

    @GetMapping
    @Operation(summary = "获取账户角色")
    public Response<List<AccountAdminRoleVO>> list(@Validated AccountAdminRoleQueryParam param) {
        return Response.ok(accountAdminRoleService.list(getProjectId(), param));
    }

    @PostMapping
    @Operation(summary = "批量绑定账户角色")
    public Response<Boolean> batchSave(@Validated @RequestBody List<AccountAdminRoleAddParam> params) {
        accountAdminRoleService.batchSave(getProjectId(), params);
        return Response.ok(true);
    }


    @DeleteMapping
    @Operation(summary = "批量删除账户角色")
    public Response<Integer> batchDelete(@Validated @RequestBody List<Long> accountAdminRoleIds){
        return Response.ok(accountAdminRoleService.batchDelete(getProjectId(), accountAdminRoleIds));
    }

}
