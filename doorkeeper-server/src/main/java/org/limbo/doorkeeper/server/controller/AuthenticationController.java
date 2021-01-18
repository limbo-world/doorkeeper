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
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.auth.AuthenticationNameCheckParam;
import org.limbo.doorkeeper.api.model.param.auth.AuthenticationUriCheckParam;
import org.limbo.doorkeeper.server.support.authc.NameAllowExecutor;
import org.limbo.doorkeeper.server.support.authc.UriAllowExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Devil
 * @date 2021/1/18 2:30 下午
 */
@Tag(name = "鉴权")
@Slf4j
@RestController
@RequestMapping("/authentication")
public class AuthenticationController extends BaseController {

    @Autowired
    private NameAllowExecutor nameAllowExecutor;

    @Autowired
    private UriAllowExecutor uriAllowExecutor;

    @GetMapping("/{clientId}/check-uri")
    @Operation(summary = "检查用户是否可以访问对应uri的资源")
    public Response<Map<Intention, List<String>>> checkByUri(@Validated AuthenticationUriCheckParam param) {
        return Response.success(uriAllowExecutor.accessAllowed(getUser().getUserId(), getClientId(), param));
    }

    @GetMapping("/{clientId}/check-name")
    @Operation(summary = "检查用户是否可以访问对应名称的资源")
    public Response<Map<Intention, List<String>>> checkByName(@Validated AuthenticationNameCheckParam param) {
        return Response.success(nameAllowExecutor.accessAllowed(getUser().getUserId(), getClientId(), param));
    }

}
