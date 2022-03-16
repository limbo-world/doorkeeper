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

package org.limbo.doorkeeper.application.service;

import org.limbo.doorkeeper.api.dto.param.LoginParam;
import org.limbo.doorkeeper.infrastructure.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.infrastructure.exception.AuthenticationException;
import org.limbo.doorkeeper.core.factory.SessionFactory;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.RealmMapper;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.UserMapper;
import org.limbo.doorkeeper.infrastructure.dto.SessionUser;
import org.limbo.doorkeeper.server.infrastructure.po.RealmPO;
import org.limbo.doorkeeper.server.infrastructure.po.UserPO;
import org.limbo.doorkeeper.server.utils.JWTUtil;
import org.limbo.utils.encryption.MD5Utils;
import org.limbo.utils.jackson.JacksonUtils;
import org.limbo.utils.verifies.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author Devil
 * @since 2020/12/31 5:33 下午
 */
@Service
public class LoginService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RealmMapper realmMapper;

    public String login(LoginParam param) {
        UserPO user;
        RealmPO tenant;
        // 不填realm 默认登录 doorkeeper
        if (param.getRealmId() == null) {
            tenant = realmMapper.getDoorkeeperRealm();
            Verifies.notNull(tenant, "realm不存在");

            user = userMapper.getByUsername(tenant.getRealmId(), param.getUsername());
            Verifies.notNull(user, "用户不存在");
            Verifies.verify(user.getIsEnabled(), "用户未启用");
            verifyPassword(param.getUsername(), param.getPassword(), param.getTimestamp(), user.getPassword());
        } else {
            user = userMapper.getByUsername(param.getRealmId(), param.getUsername());
            Verifies.notNull(user, "用户不存在");
            Verifies.verify(user.getIsEnabled(), "用户未启用");
            verifyPassword(param.getUsername(), param.getPassword(), param.getTimestamp(), user.getPassword());

            tenant = realmMapper.selectById(user.getRealmId());
            Verifies.notNull(tenant, "realm不存在");
        }
        return token(user.getUserId(), tenant.getRealmId(), user.getUsername(), user.getNickname(), tenant.getSecret());
    }

    private void verifyPassword(String username, String requirePassword, Long timestamp, String password) {
        Verifies.verify(new Date().getTime() - timestamp <= 300000, "请校对时间"); // 5分钟内的
        Verifies.verify(MD5Utils.md5AndHex(username + password + timestamp, StandardCharsets.UTF_8.name()).equals(requirePassword), "密码错误");
    }

    public String token(Long userId, Long realmId, String username, String nickname, String secret) {
        SessionUser sessionUser = SessionUser.builder()
                .userId(userId)
                .realmId(realmId)
                .username(username)
                .nickname(nickname)
                .build();

        return SessionFactory.create(JacksonUtils.toJSONString(sessionUser), secret, 7200).token();
    }

    /**
     * 根据token返回一个重置过期时间新的token（旧的token也还能使用）
     *
     * @param token
     * @return
     */
    public String refreshToken(String token) {
        try {
            Long userId = JWTUtil.getLong(token, DoorkeeperConstants.USER_ID);
            UserPO user = userMapper.selectById(userId);
            RealmPO realm = realmMapper.selectById(user.getRealmId());
            return token(user.getUserId(), user.getRealmId(), user.getUsername(), user.getNickname(), realm.getSecret());
        } catch (Exception e) {
            throw new AuthenticationException();
        }
    }

}
