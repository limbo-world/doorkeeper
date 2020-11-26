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

import org.limbo.doorkeeper.admin.model.vo.AdminAccountVO;
import org.limbo.doorkeeper.api.client.AccountClient;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.AccountAddParam;
import org.limbo.doorkeeper.api.model.param.AccountBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.AccountQueryParam;
import org.limbo.doorkeeper.api.model.vo.AccountVO;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/23 4:22 PM
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountClient accountClient;

    @PostMapping
    public Response<AccountVO> add(@RequestBody AccountAddParam param) {
        return accountClient.add(param);
    }

    @PutMapping
    public Response<Integer> update(@RequestBody AccountBatchUpdateParam param) {
        return accountClient.update(param);
    }

    @GetMapping("/query")
    public Response<Page<AccountVO>> page(AccountQueryParam param) {
        return accountClient.page(param);
    }

    @GetMapping
    public Response<List<AccountVO>> list() {
        return accountClient.list();
    }

}
