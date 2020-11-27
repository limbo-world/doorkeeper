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

import org.limbo.doorkeeper.admin.model.param.AdminAddParam;
import org.limbo.doorkeeper.admin.model.param.AdminQueryParam;
import org.limbo.doorkeeper.admin.model.param.AdminUpdateParam;
import org.limbo.doorkeeper.admin.model.vo.AdminVO;
import org.limbo.doorkeeper.admin.service.AdminService;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.vo.AccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @author Devil
 * @date 2020/11/26 7:52 PM
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping
    public Response<AccountVO> add(@RequestBody AdminAddParam param) {
        return adminService.add(param);
    }

    @PutMapping("/{accountId}")
    public Response<Integer> update(@Validated @NotNull(message = "账户不存在") @PathVariable("accountId") Long accountId,
                                    @RequestBody AdminUpdateParam param) {
        param.setAccountId(accountId);
        return adminService.update(param);
    }

    @GetMapping("/query")
    public Response<Page<AdminVO>> page(AdminQueryParam param) {
        return Response.ok(adminService.page(param));
    }

}
