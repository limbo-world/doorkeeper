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
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.ApiCheckParam;
import org.limbo.doorkeeper.api.model.param.PermissionCheckParam;
import org.limbo.doorkeeper.api.model.param.RoleCheckParam;
import org.limbo.doorkeeper.server.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuqingtong
 * @date 2020/11/24 19:34
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthenticationController extends BaseController {

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/check-api")
    @Operation(summary = "检查用户是否可以访问对应api")
    public Response<Boolean> apiCheck(ApiCheckParam param) {
        return Response.ok(authenticationService.accessApiAllowed(getProjectId(), param));
    }

    @GetMapping("/check-permission")
    @Operation(summary = "检查用户是否有对应的权限")
    public Response<Boolean> permissionCheck(PermissionCheckParam param) {
        return Response.ok(authenticationService.accessPermissionAllowed(getProjectId(), param));
    }

    @GetMapping("/check-role")
    @Operation(summary = "检查用户是否有对应的角色")
    public Response<Boolean> roleCheck(RoleCheckParam param) {
        return Response.ok(authenticationService.accessRoleAllowed(getProjectId(), param));
    }
}
