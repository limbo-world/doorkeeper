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

package org.limbo.doorkeeper.server.infrastructure.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.api.constants.UriMethod;
import org.limbo.doorkeeper.api.model.vo.ResponseVO;
import org.limbo.doorkeeper.server.infrastructure.po.UserPO;
import org.limbo.doorkeeper.server.infrastructure.mapper.UserMapper;
import org.limbo.doorkeeper.server.service.DoorkeeperService;
import org.limbo.doorkeeper.server.infrastructure.exception.AuthorizationException;
import org.limbo.doorkeeper.server.infrastructure.utils.JWTUtil;
import org.limbo.doorkeeper.server.infrastructure.utils.JacksonUtil;
import org.limbo.doorkeeper.server.infrastructure.utils.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Devil
 * @date 2020/11/24 19:29
 */
@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

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
        UserPO user = userMapper.selectById(userId);

        // 判断用户是否启用
        if (!user.getIsEnabled()) {
            WebUtil.writeToResponse(response, JacksonUtil.toJSONString(ResponseVO.unauthorized("用户未启用")));
            return false;
        }

        if (!doorkeeperService.hasUriPermission(user, request.getRequestURI(), UriMethod.parse(request.getMethod()))) {
            WebUtil.writeToResponse(response, JacksonUtil.toJSONString(ResponseVO.unauthorized(AuthorizationException.msg)));
            return false;
        }

        return true;
    }

}
