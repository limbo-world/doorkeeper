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

package org.limbo.doorkeeper.server.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.commons.lang3.time.DateUtils;
import org.limbo.doorkeeper.api.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.api.dto.command.LoginCommand;
import org.limbo.doorkeeper.server.infrastructure.exception.AuthenticationException;
import org.limbo.doorkeeper.server.infrastructure.mapper.RealmMapper;
import org.limbo.doorkeeper.server.infrastructure.mapper.UserMapper;
import org.limbo.doorkeeper.server.infrastructure.po.RealmPO;
import org.limbo.doorkeeper.server.infrastructure.po.UserPO;
import org.limbo.doorkeeper.server.infrastructure.utils.JWTUtil;
import org.limbo.doorkeeper.server.infrastructure.utils.MD5Utils;
import org.limbo.doorkeeper.server.infrastructure.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Devil
 * @date 2020/12/31 5:33 下午
 */
@Service
public class LoginService {

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private RealmMapper realmMapper;

    @Autowired
    private RealmService realmService;

    public String login(LoginCommand param) {
        UserPO user;
        RealmPO realm;
        // 不填realm 默认登录 doorkeeper
        if (param.getRealmId() == null) {
            realm = realmMapper.getDoorkeeperRealm();
            Verifies.notNull(realm, "realm不存在");

            user = userMapper.getByUsername(realm.getRealmId(), param.getUsername());
            Verifies.notNull(user, "用户不存在");
            Verifies.verify(user.getIsEnabled(), "用户未启用");
            Verifies.verify(MD5Utils.verify(param.getPassword(), user.getPassword()), "密码错误");
        } else {
            user = userMapper.getByUsername(param.getRealmId(), param.getUsername());
            Verifies.notNull(user, "用户不存在");
            Verifies.verify(user.getIsEnabled(), "用户未启用");
            Verifies.verify(MD5Utils.verify(param.getPassword(), user.getPassword()), "密码错误");

            realm = realmMapper.selectById(user.getRealmId());
            Verifies.notNull(realm, "realm不存在");
        }
        return token(user.getUserId(), realm.getRealmId(), user.getUsername(), user.getNickname(), realm.getSecret());
    }

    public String token(Long userId, Long realmId, String username, String nickname, String secret) {
        return JWT.create().withIssuer(DoorkeeperConstants.ISSUER)
                .withClaim(DoorkeeperConstants.USER_ID, userId)
                .withClaim(DoorkeeperConstants.REALM_ID, realmId)
                .withClaim(DoorkeeperConstants.USERNAME, username)
                .withClaim(DoorkeeperConstants.NICKNAME, nickname)
                .withExpiresAt(DateUtils.addHours(new Date(), 2))  //设置过期时间
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 根据token返回一个重置过期时间新的token（旧的token也还能使用）
     * @param token
     * @return
     */
    public String refreshToken(String token) {
        RealmPO realm;
        try {
            realm = realmService.getRealmByToken(token);
        } catch (Exception e) {
            throw new AuthenticationException();
        }

        return token(JWTUtil.getUserId(token), realm.getRealmId(), JWTUtil.getUsername(token), JWTUtil.getNickname(token), realm.getSecret());
    }

}
