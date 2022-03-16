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

package org.limbo.doorkeeper.server.adapter.http.controller;

import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.constants.ApiConstants;
import org.limbo.doorkeeper.infrastructure.constants.MsgConstants;
import org.limbo.doorkeeper.api.dto.vo.UserVO;
import org.limbo.doorkeeper.application.service.UserService;
import org.limbo.doorkeeper.infrastructure.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.infrastructure.exception.AuthenticationException;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.RealmMapper;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.UserMapper;
import org.limbo.doorkeeper.server.infrastructure.po.RealmPO;
import org.limbo.doorkeeper.server.infrastructure.po.UserPO;
import org.limbo.doorkeeper.server.utils.JWTUtil;
import org.limbo.utils.verifies.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Devil
 * @since 2020/11/25 10:58 AM
 */
public class BaseController {

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RealmMapper realmMapper;

    protected String getToken() {
        String token = request.getHeader(ApiConstants.TOKEN_HEADER);
        if (StringUtils.isBlank(token)) {
            throw new AuthenticationException("无认证请求");
        }
        return token;
    }

    protected UserVO getUser() {
        String token = getToken();
        try {
            UserPO user = userMapper.selectById(JWTUtil.getLong(token, DoorkeeperConstants.USER_ID));
            Verifies.notNull(user, MsgConstants.NO_USER);
            Verifies.verify(user.getIsEnabled(), MsgConstants.DISABLED_USER);

            RealmPO realm = realmMapper.selectById(user.getRealmId());
            Verifies.notNull(realm, MsgConstants.NO_REALM);

            JWTUtil.verifyToken(token, realm.getSecret());
            return userService.get(realm.getRealmId(), JWTUtil.getLong(token, DoorkeeperConstants.USER_ID), JWTUtil.getString(token, DoorkeeperConstants.USERNAME));
        } catch (Exception e) {
            throw new AuthenticationException("认证失败");
        }
    }

    protected Long getRealmId() {
        NativeWebRequest webRequest = new ServletWebRequest(request);
        Map<String, String> uriTemplateVars = (Map<String, String>) webRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        String realmId = uriTemplateVars.get(DoorkeeperConstants.REALM_ID);
        Verifies.notBlank(realmId, "realmId is null");
        return Long.valueOf(realmId);
    }

    protected Long getClientId() {
        NativeWebRequest webRequest = new ServletWebRequest(request);
        Map<String, String> uriTemplateVars = (Map<String, String>) webRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        String clientId = uriTemplateVars.get(DoorkeeperConstants.NAMESPACE_ID);
        Verifies.notBlank(clientId, "groupId is null");
        return Long.valueOf(clientId);
    }

}
