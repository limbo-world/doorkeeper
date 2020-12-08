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
import org.limbo.doorkeeper.api.model.param.AccountRoleAddParam;
import org.limbo.doorkeeper.api.model.param.AccountRoleQueryParam;
import org.limbo.doorkeeper.api.model.vo.AccountRoleVO;
import org.limbo.doorkeeper.server.service.AccountRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author Devil
 * @date 2020/11/27 10:21 AM
 */
@Tag(name = "账户角色")
@RestController
@RequestMapping("/account-role")
public class AccountRoleController extends BaseController {

    @Autowired
    private AccountRoleService accountRoleService;

    @GetMapping
    @Operation(summary = "获取账户角色")
    public Response<List<AccountRoleVO>> list(@Validated AccountRoleQueryParam param) {
        return Response.ok(accountRoleService.list(getProjectId(), param));
    }

    @PostMapping
    @Operation(summary = "批量新增账户角色")
    public Response<Boolean> batchSave(@Validated @RequestBody List<AccountRoleAddParam> params) {
        accountRoleService.batchSave(getProjectId(), params);
        return Response.ok(true);
    }


    @DeleteMapping
    @Operation(summary = "批量删除账户角色")
    public Response<Integer> batchDelete(@Validated @RequestBody List<Long> accountRoleIds){
        return Response.ok(accountRoleService.batchDelete(getProjectId(), accountRoleIds));
    }

}
