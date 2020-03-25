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
import org.limbo.authc.admin.dubbo.consumers.AuthenticationDubboConsumer;
import org.limbo.authc.admin.support.autoconfig.AuthcProperties;
import org.limbo.authc.admin.support.blog.BLog;
import org.limbo.authc.admin.support.blog.BLogType;
import org.limbo.authc.api.interfaces.beans.Param;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.AccountVO;
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
@RequestMapping("/admin")
public class AdminController extends BaseController {

    @Autowired
    private AccountDubboConsumer accountDubboConsumer;
    @Autowired
    private AuthenticationDubboConsumer authenticationDubboConsumer;

    @Autowired
    private AuthcProperties authcProperties;

    @GetMapping
    public Response<List<AccountVO>> list() {
        return accountDubboConsumer.list(new Param(getSessionAccount().getAccountProjectId()));
    }

    @PostMapping
    @BLog(name = "新增管理员", type = BLogType.CREATE)
    public Response<AccountVO> add(@RequestBody @Validated AccountVO.RegisterParam param) {
        return authenticationDubboConsumer.registerAccount(param);
    }

    @PutMapping("/{accountId}")
    @BLog(name = "修改管理员信息", type = BLogType.UPDATE)
    public Response<AccountVO> update(
            @RequestBody @Validated AccountVO.UpdateParam param,
            @Validated @PathVariable("accountId") @NotNull(message = "账户不存在") Long accountId
    ) {
        Long sessionAccountId = getSessionAccount().getAccountId();
        Verifies.equals(sessionAccountId, accountId, "无权执行此操作");
        param.setAccountId(accountId);
        param.setProjectId(getSessionAccount().getAccountProjectId());
        return accountDubboConsumer.update(param);
    }

    @PutMapping("/{accountId}/password")
    @BLog(name = "修改密码", type = BLogType.UPDATE)
    public Response<AccountVO> updatePassword(
            @RequestBody AccountVO.UpdatePasswordParam param,
            @Validated @PathVariable("accountId") @NotNull(message = "用户不存在") Long accountId) {
        Long sessionAccountId = getSessionAccount().getAccountId();
        Verifies.equals(sessionAccountId, accountId, "无权执行此操作");
        param.setAccountId(accountId);
        return accountDubboConsumer.updatePassword(param);
    }

    @DeleteMapping("/{accountId}")
    @BLog(name = "删除管理员", type = BLogType.DELETE)
    public Response<AccountVO> delete(@Validated @PathVariable("accountId") @NotNull(message = "用户不存在") Long accountId) {
        return accountDubboConsumer.deleteAccount(new AccountVO.DeleteParam(authcProperties.getProjectId(), accountId));
    }

}
