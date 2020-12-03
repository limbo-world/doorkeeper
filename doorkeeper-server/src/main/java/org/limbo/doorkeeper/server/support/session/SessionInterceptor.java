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

import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.server.support.config.DoorkeeperProperties;
import org.limbo.doorkeeper.server.support.session.exception.SessionException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.CompletableFuture;

/**
 * @author liuqingtong
 * @date 2020/11/24 15:54
 */
@Slf4j
public class SessionInterceptor implements HandlerInterceptor {

    private final DoorkeeperProperties doorkeeperProperties;

    private final RedisSessionDAO redisSessionDAO;

    public SessionInterceptor(DoorkeeperProperties doorkeeperProperties, RedisSessionDAO redisSessionDAO) {
        this.doorkeeperProperties = doorkeeperProperties;
        this.redisSessionDAO = redisSessionDAO;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 验证会话存在
        String token = request.getHeader(doorkeeperProperties.getSession().getHeaderName());
        AbstractSession session = redisSessionDAO.readSessionMayNull(token);
        if (session == null) {
            throw new SessionException("无有效会话");
        }
        // 会话正常，则更新会话过期时间，异步执行
        CompletableFuture.runAsync(() -> redisSessionDAO.touchSession(token));
        return true;
    }
}
