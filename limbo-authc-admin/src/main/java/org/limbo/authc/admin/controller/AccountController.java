/*
 *  Copyright 2020-2024 Limbo Team (https://github.com/LimboHome).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.limbo.authc.admin.controller;

import org.limbo.authc.admin.dubbo.consumers.AccountDubboConsumer;
import org.limbo.authc.admin.dubbo.consumers.AuthorizationDubboConsumer;
import org.limbo.authc.admin.support.blog.BLog;
import org.limbo.authc.admin.support.blog.BLogType;
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.Param;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.AccountVO;
import org.limbo.authc.api.interfaces.beans.vo.AuthorizationVO;
import org.limbo.authc.api.interfaces.utils.verify.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/9 2:32 PM
 * @email brozen@qq.com
 */
@RestController
@RequestMapping("/account")
public class AccountController extends BaseController {

    @Autowired
    private AccountDubboConsumer accountDubboConsumer;

    @Autowired
    private AuthorizationDubboConsumer authorizationDubboConsumer;

    @GetMapping
    public Response<List<AccountVO>> list() {
        Param param = new Param(currentProjectId());
        return accountDubboConsumer.list(param);
    }

    @GetMapping("/query")
    public Response<Page<AccountVO>> query(AccountVO.QueryParam param) {
        param.setProjectId(currentProjectId());
        return accountDubboConsumer.query(param);
    }

    @GetMapping("/{accountId}")
    public Response<AccountVO> get(@Validated @PathVariable("accountId") @NotNull(message = "账户不存在") Long accountId) {
        AccountVO.GetParam param = new AccountVO.GetParam(currentProjectId(), accountId);
        return accountDubboConsumer.get(param);
    }

    @PutMapping("/{accountId}/grant")
    @BLog(expression = "'修改账户授权 '.concat(#arg1)", type = BLogType.UPDATE)
    public Response<Boolean> updateGrant(
            @RequestBody AuthorizationVO.AccountGrantParam param,
            @Validated @PathVariable("accountId") @NotNull(message = "账户不存在") Long accountId
    ) {
        Verifies.verify(accountId.equals(param.getAccountId()));
        param.setProjectId(currentProjectId());
        return authorizationDubboConsumer.updateGrant(param);
    }

}
