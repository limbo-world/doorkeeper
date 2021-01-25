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
import org.limbo.doorkeeper.api.model.param.auth.AuthorizationNameCheckParam;
import org.limbo.doorkeeper.api.model.param.auth.AuthorizationTagCheckParam;
import org.limbo.doorkeeper.api.model.param.auth.AuthorizationUriCheckParam;
import org.limbo.doorkeeper.api.model.vo.AuthorizationCheckResult;
import org.limbo.doorkeeper.server.support.auth.checker.AuthorizationCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Devil
 * @date 2021/1/18 2:30 下午
 */
@Tag(name = "鉴权")
@Slf4j
@RestController
@RequestMapping("/authorization")
public class AuthorizationController extends BaseController {

    @Autowired
    private AuthorizationCheckerFactory authorizationCheckerFactory;

    @RequestMapping("/check-uri")
    @Operation(summary = "检查用户是否可以访问对应uri的资源")
    public Response<AuthorizationCheckResult> checkByUri(@RequestBody @Validated AuthorizationUriCheckParam param) {
        param.setUserId(getUser().getUserId());
        return Response.success(authorizationCheckerFactory.newUriAuthorizationChecker(param).check());
    }

    @RequestMapping("/check-name")
    @Operation(summary = "检查用户是否可以访问对应名称的资源")
    public Response<AuthorizationCheckResult> checkByName(@RequestBody @Validated AuthorizationNameCheckParam param) {
        param.setUserId(getUser().getUserId());
        return Response.success(authorizationCheckerFactory.newNameAuthorizationChecker(param).check());
    }

    @RequestMapping("/check-tag")
    @Operation(summary = "检查用户是否可以访问对应标签的资源")
    public Response<AuthorizationCheckResult> checkByTag(@RequestBody @Validated AuthorizationTagCheckParam param) {
        param.setUserId(getUser().getUserId());
        return Response.success(authorizationCheckerFactory.newTagAuthorizationChecker(param).check());
    }

}
