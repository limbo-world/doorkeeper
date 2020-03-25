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
public interface AuthenticationApi {

    /**
     * 注册用户
     * @return  返回注册成功的用户信息，返回值不包括密码信息
     */
    Response<AccountVO> register(AccountVO.RegisterParam param);


    /**
     * 注册用户 补偿
     */
    Response<AccountVO> prepareRegister(AccountVO.RegisterParam param);

    /**
     * 提交用户注册事务
     */
    Response<Boolean> commitRegister(AccountVO.DeleteParam param);

    /**
     * 检查传入的username是否已经存在
     */
    Response<Boolean> isUsernameExist(AccountVO.UsernameCheckParam param);

    /**
     * 用户认证，登录操作
     */
    Response<AccountVO> authenticate(AccountVO.LoginParam param);

}
