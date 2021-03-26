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

package org.limbo.doorkeeper.server.controller.realm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.check.*;
import org.limbo.doorkeeper.api.model.vo.GroupVO;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.limbo.doorkeeper.api.model.vo.UserVO;
import org.limbo.doorkeeper.api.model.vo.check.AuthorizationCheckResult;
import org.limbo.doorkeeper.api.model.vo.check.GroupCheckResult;
import org.limbo.doorkeeper.api.model.vo.check.RoleCheckResult;
import org.limbo.doorkeeper.server.controller.BaseController;
import org.limbo.doorkeeper.server.service.GroupUserService;
import org.limbo.doorkeeper.server.service.UserRoleService;
import org.limbo.doorkeeper.server.service.UserService;
import org.limbo.doorkeeper.server.support.auth.checker.AuthorizationCheckerFactory;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;
import java.util.Map;

/**
 * @author Devil
 * @date 2021/1/18 2:30 下午
 */
@Tag(name = "管理端用户鉴权")
@Slf4j
@RestController
@RequestMapping("/api/admin/realm/{realmId}/user/{userId}")
public class AdminUserAuthorizationController extends BaseController {

    @Autowired
    private AuthorizationCheckerFactory authorizationCheckerFactory;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private GroupUserService groupUserService;

    @Autowired
    private UserService userService;

    @PostMapping("/resource/check-uri")
    @Operation(summary = "检查用户是否可以访问对应uri的资源")
    public Response<AuthorizationCheckResult> checkResourceByUri(@RequestBody @Validated AuthorizationUriCheckParam param) {
        param.setUserId(getPathUserId());
        return Response.success(authorizationCheckerFactory.newUriAuthorizationChecker(param).check());
    }

    @PostMapping("/resource/check-name")
    @Operation(summary = "检查用户是否可以访问对应名称的资源")
    public Response<AuthorizationCheckResult> checkResourceByName(@RequestBody @Validated AuthorizationNameCheckParam param) {
        param.setUserId(getPathUserId());
        return Response.success(authorizationCheckerFactory.newNameAuthorizationChecker(param).check());
    }

    @PostMapping("/resource/check-tag")
    @Operation(summary = "检查用户是否可以访问对应标签的资源")
    public Response<AuthorizationCheckResult> checkResourceByTag(@RequestBody @Validated AuthorizationTagCheckParam param) {
        param.setUserId(getPathUserId());
        return Response.success(authorizationCheckerFactory.newTagAuthorizationChecker(param).check());
    }

    @PostMapping("/resource/check-all")
    @Operation(summary = "检查并返回用户可以的所有资源")
    public Response<AuthorizationCheckResult> checkAllResource(@RequestBody @Validated AuthorizationAllCheckParam param) {
        param.setUserId(getPathUserId());
        return Response.success(authorizationCheckerFactory.newAllAuthorizationChecker(param).check());
    }

    @PostMapping("/role/check")
    @Operation(summary = "检查用户拥有的角色资源")
    public Response<RoleCheckResult> checkRole(@RequestBody @Validated RoleCheckParam param) {
        UserVO user = userService.get(getRealmId(), getPathUserId(), null);
        List<RoleVO> roles = userRoleService.checkRole(user.getUserId(), user.getRealmId(), param);
        RoleCheckResult result = new RoleCheckResult();
        result.setRoles(roles);
        return Response.success(result);
    }

    @PostMapping("/group/check")
    @Operation(summary = "检查用户所在的组")
    public Response<GroupCheckResult> checkGroup(@RequestBody @Validated GroupCheckParam param) {
        UserVO user = userService.get(getRealmId(), getPathUserId(), null);
        List<GroupVO> groups = groupUserService.checkGroup(user.getUserId(), user.getRealmId(), param);
        GroupCheckResult result = new GroupCheckResult();
        result.setGroups(groups);
        return Response.success(result);
    }

    private static final String USER_ID = "userId";

    private Long getPathUserId() {
        NativeWebRequest webRequest = new ServletWebRequest(request);
        Map<String, String> uriTemplateVars = (Map<String, String>) webRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        String clientId = uriTemplateVars.get(USER_ID);
        Verifies.notBlank(clientId, "请选择用户");
        return Long.valueOf(clientId);
    }

}
