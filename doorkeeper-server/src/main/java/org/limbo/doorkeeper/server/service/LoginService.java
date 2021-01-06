/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   	http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.limbo.doorkeeper.server.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.limbo.doorkeeper.api.model.param.LoginParam;
import org.limbo.doorkeeper.api.model.vo.SessionUser;
import org.limbo.doorkeeper.server.dao.UserMapper;
import org.limbo.doorkeeper.server.entity.User;
import org.limbo.doorkeeper.server.support.session.RedisSessionDAO;
import org.limbo.doorkeeper.server.utils.MD5Utils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Devil
 * @date 2020/12/31 5:33 下午
 */
@Service
public class LoginService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisSessionDAO sessionDAO;

    public SessionUser login(LoginParam param) {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getRealmId, param.getRealmId())
                .eq(User::getUsername, param.getUsername())
        );
        Verifies.notNull(user, "用户不存在");
        Verifies.verify(user.getIsEnabled(), "用户未启用");
        Verifies.verify(MD5Utils.verify(param.getPassword(), user.getPassword()), "密码错误");

        SessionUser sessionUser = new SessionUser();
        sessionUser.setUserId(user.getUserId());
        sessionUser.setNickname(user.getNickname());
        return sessionDAO.createSession(sessionUser);
    }
}
