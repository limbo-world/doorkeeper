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

package org.limbo.doorkeeper.server.support.auth;

import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.api.constants.UriMethod;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.check.ResourceCheckParam;
import org.limbo.doorkeeper.api.model.vo.check.ResourceCheckResult;
import org.limbo.doorkeeper.server.dal.entity.Client;
import org.limbo.doorkeeper.server.dal.entity.Realm;
import org.limbo.doorkeeper.server.dal.entity.User;
import org.limbo.doorkeeper.server.dal.mapper.ClientMapper;
import org.limbo.doorkeeper.server.dal.mapper.RealmMapper;
import org.limbo.doorkeeper.server.dal.mapper.UserMapper;
import org.limbo.doorkeeper.server.service.DoorkeeperService;
import org.limbo.doorkeeper.server.utils.JWTUtil;
import org.limbo.doorkeeper.server.utils.JacksonUtil;
import org.limbo.doorkeeper.server.utils.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;

/**
 * @author Devil
 * @date 2020/11/24 19:29
 */
@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RealmMapper realmMapper;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private ResourceChecker resourceChecker;

    @Autowired
    private DoorkeeperService doorkeeperService;

    /**
     * 校验管理端权限 匹配 /admin/realm/**
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 判断 url 是否有对应权限
        String token = request.getHeader(DoorkeeperConstants.TOKEN_HEADER);
        Long userId = JWTUtil.getUserId(token);
        User user = userMapper.selectById(userId);

        // 判断用户是否启用
        if (!user.getIsEnabled()) {
            WebUtil.writeToResponse(response, JacksonUtil.toJSONString(Response.unauthorized("用户未启用")));
            return false;
        }

        Realm doorkeeperRealm = realmMapper.getDoorkeeperRealm();

        // 判断用户是否属于doorkeeper域或公有域
        if (!doorkeeperRealm.getRealmId().equals(user.getRealmId())) {
            WebUtil.writeToResponse(response, JacksonUtil.toJSONString(Response.unauthorized(AuthorizationException.msg)));
            return false;
        }

        // 超级管理员认证
        if (doorkeeperService.isSuperAdmin(userId)) {
            return true;
        }

        // 判断uri权限
        NativeWebRequest webRequest = new ServletWebRequest(request);
        Map<String, String> uriTemplateVars = (Map<String, String>) webRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        String realmIdStr = uriTemplateVars.get(DoorkeeperConstants.REALM_ID);

        Realm realm = realmMapper.selectById(Long.valueOf(realmIdStr));

        // 获取对应的client
        Client client = clientMapper.getByName(doorkeeperRealm.getRealmId(), realm.getName());
        ResourceCheckParam checkParam = new ResourceCheckParam()
                .setClientId(client.getClientId())
                .setUris(Collections.singletonList(UriMethod.parse(request.getMethod()) + DoorkeeperConstants.KV_DELIMITER + request.getRequestURI()));
        ResourceCheckResult checkResult = resourceChecker.check(userId, true, checkParam);

        if (checkResult.getResources().size() <= 0) {
            WebUtil.writeToResponse(response, JacksonUtil.toJSONString(Response.unauthorized(AuthorizationException.msg)));
            return false;
        }

        return true;
    }

}
