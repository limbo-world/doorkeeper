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

package org.limbo.doorkeeper.server.adapter.http.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.constants.ApiConstants;
import org.limbo.doorkeeper.api.constants.MsgConstants;
import org.limbo.doorkeeper.api.dto.vo.ResponseVO;
import org.limbo.doorkeeper.server.infrastructure.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.server.infrastructure.mapper.RealmMapper;
import org.limbo.doorkeeper.server.infrastructure.mapper.UserMapper;
import org.limbo.doorkeeper.server.infrastructure.po.RealmPO;
import org.limbo.doorkeeper.server.infrastructure.po.UserPO;
import org.limbo.doorkeeper.server.infrastructure.exception.AuthenticationException;
import org.limbo.doorkeeper.server.infrastructure.utils.JWTUtil;
import org.limbo.doorkeeper.server.infrastructure.utils.JacksonUtil;
import org.limbo.doorkeeper.server.infrastructure.utils.Verifies;
import org.limbo.doorkeeper.server.infrastructure.utils.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author liuqingtong
 * @since 2020/11/24 15:54
 */
@Slf4j
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RealmMapper realmMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 验证会话存在
        String token = request.getHeader(ApiConstants.TOKEN_HEADER);
        if (StringUtils.isBlank(token)) {
            WebUtil.writeToResponse(response, JacksonUtil.toJSONString(ResponseVO.unauthenticated("无认证请求")));
            return false;
        }
        try {
            Long userId = JWTUtil.getLong(token, DoorkeeperConstants.USER_ID);
            UserPO user = userMapper.selectById(userId);
            Verifies.notNull(user, MsgConstants.NO_USER);
            Verifies.verify(user.getIsEnabled(), MsgConstants.DISABLED_USER);

            RealmPO realm = realmMapper.selectById(user.getRealmId());
            Verifies.notNull(realm, MsgConstants.NO_REALM);

            JWTUtil.verifyToken(token, realm.getSecret());
        } catch (Exception e) {
            WebUtil.writeToResponse(response, JacksonUtil.toJSONString(ResponseVO.unauthenticated(AuthenticationException.msg)));
            return false;
        }

        return true;
    }
}
