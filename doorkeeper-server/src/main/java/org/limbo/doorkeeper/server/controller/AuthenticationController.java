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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.AuthenticationCheckParam;
import org.limbo.doorkeeper.api.model.param.AccountGrantInfoGetParam;
import org.limbo.doorkeeper.api.model.vo.AccountGrantVO;
import org.limbo.doorkeeper.server.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuqingtong
 * @date 2020/11/24 19:34
 */
@Tag(name = "鉴权")
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthenticationController extends BaseController {

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/check")
    @Operation(summary = "检查用户是否可以访问对应api")
    public Response<Boolean> check(AuthenticationCheckParam param) {
        return Response.ok(authenticationService.accessAllowed(getProjectId(), param));
    }

    @GetMapping("/grant-info")
    @Operation(summary = "获取用户的授权信息，返回授予的角色、权限")
    public Response<AccountGrantVO> grantInfo(AccountGrantInfoGetParam param) {
        return Response.ok(authenticationService.getGrantInfo(getProjectId(), param.getAccountId()));
    }
}
