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
import org.limbo.doorkeeper.api.constants.HeaderConstants;
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.api.constants.PolicyType;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.check.AuthorizationUriCheckParam;
import org.limbo.doorkeeper.api.model.vo.check.AuthorizationCheckResult;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyRoleVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.server.dal.entity.Client;
import org.limbo.doorkeeper.server.dal.entity.Realm;
import org.limbo.doorkeeper.server.dal.entity.Role;
import org.limbo.doorkeeper.server.dal.entity.User;
import org.limbo.doorkeeper.server.dal.mapper.ClientMapper;
import org.limbo.doorkeeper.server.dal.mapper.RealmMapper;
import org.limbo.doorkeeper.server.dal.mapper.RoleMapper;
import org.limbo.doorkeeper.server.dal.mapper.UserMapper;
import org.limbo.doorkeeper.server.support.auth.checker.AuthorizationCheckerFactory;
import org.limbo.doorkeeper.server.support.auth.policies.PolicyCheckerFactory;
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
    private RoleMapper roleMapper;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private AuthorizationCheckerFactory authorizationCheckerFactory;

    @Autowired
    private PolicyCheckerFactory policyCheckerFactory;

    /**
     * 校验管理端权限 匹配 /admin/realm/**
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 判断 url 是否有对应权限
        String token = request.getHeader(HeaderConstants.TOKEN_HEADER);
        Long userId = JWTUtil.getUserId(token);
        User user = userMapper.selectById(userId);

        Realm doorkeeperRealm = realmMapper.getDoorkeeperRealm();

        // 判断用户是否属于doorkeeper域或公有域
        if (!doorkeeperRealm.getRealmId().equals(user.getRealmId())) {
            WebUtil.writeToResponse(response, JacksonUtil.toJSONString(Response.unauthorized(AuthorizationException.msg)));
            return false;
        }

        // 超级管理员认证
        Role doorkeeperAdmin = roleMapper.getByName(doorkeeperRealm.getRealmId(), DoorkeeperConstants.DEFAULT_ID, DoorkeeperConstants.ADMIN);
        PolicyRoleVO policyRoleVO = new PolicyRoleVO();
        policyRoleVO.setRoleId(doorkeeperAdmin.getRoleId());
        policyRoleVO.setIsEnabled(doorkeeperAdmin.getIsEnabled());
        PolicyVO adminPolicy = new PolicyVO();
        adminPolicy.setRealmId(doorkeeperRealm.getRealmId());
        adminPolicy.setLogic(Logic.ALL.getValue());
        adminPolicy.setType(PolicyType.ROLE.getValue());
        adminPolicy.setIntention(Intention.ALLOW.getValue());
        adminPolicy.setRoles(Collections.singletonList(policyRoleVO));
        AuthorizationUriCheckParam adminCheckParam = new AuthorizationUriCheckParam();
        adminCheckParam.setUserId(user.getUserId());
        Intention policyCheckIntention = policyCheckerFactory.newPolicyChecker(adminPolicy).check(adminCheckParam);
        if (Intention.ALLOW == policyCheckIntention) {
            return true;
        }

        // 判断uri权限
        NativeWebRequest webRequest = new ServletWebRequest(request);
        Map<String, String> uriTemplateVars = (Map<String, String>) webRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        String realmIdStr = uriTemplateVars.get(DoorkeeperConstants.REALM_ID);

        Realm realm = realmMapper.selectById(Long.valueOf(realmIdStr));

        // 获取对应的client
        Client client = clientMapper.getByName(doorkeeperRealm.getRealmId(), realm.getName());


        AuthorizationUriCheckParam checkParam = new AuthorizationUriCheckParam()
                .setUserId(userId).setClientId(client.getClientId())
                .setResourceAssigner(Collections.singletonList(request.getRequestURI()));
        AuthorizationCheckResult checkResult = authorizationCheckerFactory.newUriAuthorizationChecker(checkParam).check();

        if (checkResult.getResources().size() <= 0) {
            WebUtil.writeToResponse(response, JacksonUtil.toJSONString(Response.unauthorized(AuthorizationException.msg)));
        }

        return true;
    }

}
