/*
 *  Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
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

package org.limbo.doorkeeper.server.service;

import org.limbo.authc.api.interfaces.beans.vo.AccountVO;

/**
 * @author Brozen
 * @date 2020/3/9 9:29 AM
 * @email brozen@qq.com
 */
public interface AuthenticationService {

    /**
     * 注册用户
     */
    AccountVO register(AccountVO.RegisterParam account, Boolean isActivated);

    /**
     * 检查传入的username是否已经存在
     */
    Boolean isUsernameExist(AccountVO.UsernameCheckParam param);

    /**
     * 用户认证，登录操作
     */
    AccountVO authenticate(AccountVO.LoginParam param);
}
