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

package org.limbo.doorkeeper.server.controller;

import org.limbo.doorkeeper.api.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.api.model.vo.SessionAccount;
import org.limbo.doorkeeper.server.support.session.AbstractSessionDAO;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Devil
 * @date 2020/11/25 10:58 AM
 */
public class BaseController {

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected AbstractSessionDAO sessionDAO;

    protected Long getAccountId() {
        return getSession().getAccountId();
    }

    protected Long getProjectId() {
        String projectIdStr = request.getHeader(DoorkeeperConstants.PROJECT_HEADER);
        Verifies.notBlank(projectIdStr, "当前操作项目为空");
        return Long.valueOf(projectIdStr);
    }

    protected SessionAccount getSession() {
        String sessionId = request.getHeader(DoorkeeperConstants.SESSION_HEADER);
        return sessionDAO.readSession(sessionId);
    }

}
