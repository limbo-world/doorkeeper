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

package org.limbo.doorkeeper.server.support.session;

import com.auth0.jwt.JWT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.constants.SessionConstants;
import org.limbo.doorkeeper.server.dao.RealmMapper;
import org.limbo.doorkeeper.server.dao.UserMapper;
import org.limbo.doorkeeper.server.entity.Realm;
import org.limbo.doorkeeper.server.entity.User;
import org.limbo.doorkeeper.server.support.session.exception.SessionException;
import org.limbo.doorkeeper.server.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author liuqingtong
 * @date 2020/11/24 15:54
 */
@Slf4j
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RealmMapper realmMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 验证会话存在
        String token = request.getHeader(SessionConstants.TOKEN_HEADER);
        if (StringUtils.isBlank(token)) {
            throw new SessionException("无认证请求");
        }
        Long userId = JWT.decode(token).getClaim("userId").asLong();
        try {
            User user = userMapper.selectById(userId);
            Realm realm = realmMapper.selectById(user.getRealmId());
            JWTUtil.verifyToken(token, realm.getSecret());
        } catch (Exception e) {
            throw new SessionException();
        }

        return true;
    }
}
