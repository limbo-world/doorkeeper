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

package org.limbo.authc.api.interfaces.apis;

import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.AccountVO;

/**
 * 认证相关接口
 *
 * @author Brozen
 * @date 2020/3/7 4:27 PM
 * @email brozen@qq.com
 */
public class AuthenticationApiMock implements AuthenticationApi {


    @Override
    public Response<AccountVO> register(AccountVO.RegisterParam account) {
        return Response.serviceError("服务暂不可用！");
    }

    @Override
    public Response<AccountVO> prepareRegister(AccountVO.RegisterParam param) {
        return Response.serviceError("服务暂不可用！");
    }

    @Override
    public Response<Boolean> commitRegister(AccountVO.DeleteParam param) {
        return Response.serviceError("服务暂不可用！");
    }

    @Override
    public Response<Boolean> isUsernameExist(AccountVO.UsernameCheckParam param) {
        return Response.serviceError("服务暂不可用！");
    }

    @Override
    public Response<AccountVO> authenticate(AccountVO.LoginParam param) {
        return Response.serviceError("服务暂不可用！");
    }
}
