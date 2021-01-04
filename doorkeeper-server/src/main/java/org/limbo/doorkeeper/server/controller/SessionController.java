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

package org.limbo.doorkeeper.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.vo.AccountGrantVO;
import org.limbo.doorkeeper.api.model.vo.SessionUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Devil
 * @date 2021/1/4 10:45 上午
 */
@Tag(name = "会话")
@Slf4j
@RestController
@RequestMapping("/session")
public class SessionController extends BaseController {

    @Operation(summary = "获取会话")
    @GetMapping
    public Response<SessionUser> session() {
        SessionUser session = getSession();
        return Response.success(session);
    }

    @Operation(summary = "获取当前项目页面权限信息")
    @GetMapping("/grant-info")
    public Response<AccountGrantVO> getGrantInfo() {
        // 拿到用户管理端权限
        return Response.success();
    }

    @Operation(summary = "注销账户")
    @GetMapping("/logout")
    public Response<Boolean> logout() {
        SessionUser session = getSession();
        sessionDAO.destroySession(session.getSessionId());
        return Response.success(true);
    }


}
