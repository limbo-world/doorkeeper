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

package org.limbo.doorkeeper.server.support.authc;

import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.model.param.AuthenticationCheckParam;
import org.limbo.doorkeeper.server.service.AuthenticationService;
import org.limbo.doorkeeper.server.support.config.DoorkeeperProperties;
import org.limbo.doorkeeper.server.support.session.AbstractSession;
import org.limbo.doorkeeper.server.support.session.RedisSessionDAO;
import org.limbo.doorkeeper.server.support.session.exception.SessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Devil
 * @date 2020/11/24 19:29
 */
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final DoorkeeperProperties doorkeeperProperties;

    private final RedisSessionDAO redisSessionDAO;

    @Autowired
    private AuthenticationService authenticationService;

    public AuthenticationInterceptor(DoorkeeperProperties doorkeeperProperties, RedisSessionDAO redisSessionDAO) {
        this.doorkeeperProperties = doorkeeperProperties;
        this.redisSessionDAO = redisSessionDAO;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 判断 url 是否有对应权限
        String sessionId = request.getHeader(doorkeeperProperties.getSession().getHeaderName());
        AbstractSession adminSession = redisSessionDAO.readSessionMayNull(sessionId);
        if (adminSession == null) {
            throw new SessionException("无有效会话");
        }

        AuthenticationCheckParam param = new AuthenticationCheckParam();
        param.setAccountId(adminSession.getAccount().getAccountId());
        param.setMethod(request.getMethod());
        param.setPath(request.getServletPath());
        // todo
        return authenticationService.accessAllowed(0L, param);
    }
}
