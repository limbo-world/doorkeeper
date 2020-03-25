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

import org.limbo.authc.admin.service.AdminBLogService;
import org.limbo.authc.admin.support.autoconfig.AuthcProperties;
import org.limbo.authc.admin.support.session.AdminSession;
import org.limbo.authc.admin.support.session.RedisSessionDAO;
import org.limbo.authc.session.SessionAccount;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Brozen
 * @date 2020/3/9 2:33 PM
 * @email brozen@qq.com
 */
public class BaseController {

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected HttpServletResponse response;

    @Autowired
    protected AuthcProperties authcProperties;

    @Autowired
    protected RedisSessionDAO sessionDAO;

    @Autowired
    protected AdminBLogService bLogService;

    protected AdminSession getSession() {
        String sessionId = request.getHeader(authcProperties.getSession().getHeaderName());
        return sessionDAO.readSession(sessionId);
    }

    protected SessionAccount getSessionAccount() {
        return getSession().getAccount();
    }

    protected Long currentProjectId() {
        return getSessionAccount().getCurrentProjectId();
    }


    protected String clientIp() {
        if (request.getHeader("x-forwarded-for") == null) {
            return request.getRemoteAddr();
        } else {
            return request.getHeader("x-forwarded-for");
        }
    }
}
