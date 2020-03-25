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

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.limbo.authc.api.interfaces.apis.AuthenticationApi;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.po.AccountPO;
import org.limbo.authc.api.interfaces.beans.vo.AccountVO;
import org.limbo.authc.core.service.AccountService;
import org.limbo.authc.core.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Brozen
 * @date 2020/3/9 9:16 AM
 * @email brozen@qq.com
 */
@Slf4j
@Service(version = "${dubbo.service.version}", validation = "true")
public class AuthenticationApiDubboProvider extends BaseProvider implements AuthenticationApi {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private AccountService accountService;

    @Override
    public Response<AccountVO> register(AccountVO.RegisterParam param) {
        return Response.ok(authenticationService.register(param, true));
    }

    @Override
    public Response<AccountVO> prepareRegister(AccountVO.RegisterParam param) {
        return Response.ok(authenticationService.register(param, false));
    }

    @Override
    public Response<Boolean> commitRegister(AccountVO.DeleteParam param) {
        // 更新激活状态
        AccountPO account = new AccountPO();
        account.setProjectId(param.getProjectId());
        account.setAccountId(param.getAccountId());
        account.setIsActivated(true);
        accountService.updateById(account);
        return Response.ok(true);
    }

    @Override
    public Response<Boolean> isUsernameExist(AccountVO.UsernameCheckParam param) {
        return Response.ok(authenticationService.isUsernameExist(param));
    }

    @Override
    public Response<AccountVO> authenticate(AccountVO.LoginParam param) {
        return Response.ok(authenticationService.authenticate(param));
    }

}
