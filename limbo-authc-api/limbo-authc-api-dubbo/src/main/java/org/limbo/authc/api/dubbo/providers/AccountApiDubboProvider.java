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

package org.limbo.authc.api.dubbo.providers;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.limbo.authc.api.interfaces.apis.AccountApi;
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.Param;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.AccountVO;
import org.limbo.authc.core.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/9 9:15 AM
 * @email brozen@qq.com
 */
@Slf4j
@Service(version = "${dubbo.service.version}", validation = "true")
public class AccountApiDubboProvider extends BaseProvider implements AccountApi {

    @Autowired
    private AccountService accountService;

    @Override
    public Response<AccountVO> update(AccountVO.UpdateParam param) {
        accountService.updateAccount(param);
        return Response.ok(accountService.get(new AccountVO.GetParam(param.getProjectId(), param.getAccountId())));
    }

    @Override
    public Response<AccountVO> updatePassword(AccountVO.UpdatePasswordParam param) {
        accountService.updatePassword(param);
        return Response.ok(accountService.get(new AccountVO.GetParam(param.getProjectId(), param.getAccountId())));
    }

    @Override
    public Response<AccountVO> delete(AccountVO.DeleteParam param) {
        AccountVO account = accountService.get(new AccountVO.GetParam(param.getProjectId(), param.getAccountId()));
        accountService.deleteAccount(param);
        return Response.ok(account);
    }

    @Override
    public Response<Boolean> cancelDelete(AccountVO.DeleteParam param) {
        // 回滚失败的话，人工处理下
        log.error("尝试账户删除回滚：" + JSONObject.toJSONString(param));
        accountService.unDeleteAccount(param);
        return Response.ok(true);
    }

    @Override
    public Response<List<AccountVO>> list(Param param) {
        return Response.ok(accountService.listAccounts(param.getProjectId()));
    }

    @Override
    public Response<Page<AccountVO>> query(AccountVO.QueryParam param) {
        return Response.ok(accountService.queryAccount(param));
    }

    @Override
    public Response<AccountVO> get(AccountVO.GetParam param) {
        return Response.ok(accountService.get(param));
    }

}
