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

package org.limbo.doorkeeper.server.infrastructure.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.limbo.doorkeeper.api.constants.DoorkeeperConstants;

/**
 * @author Devil
 * @since 2021/1/15 7:43 下午
 */
public class JWTUtil {

    public static boolean verifyToken(String token, String secret) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secret)).build();
        jwtVerifier.verify(token);
        return true;
    }

    public static Long getUserId(String token) {
        return JWT.decode(token).getClaim(DoorkeeperConstants.USER_ID).asLong();
    }

    public static String getUsername(String token) {
        return JWT.decode(token).getClaim(DoorkeeperConstants.USERNAME).asString();
    }

    public static String getNickname(String token) {
        return JWT.decode(token).getClaim(DoorkeeperConstants.NICKNAME).asString();
    }

    public static Long getRealmId(String token) {
        return JWT.decode(token).getClaim(DoorkeeperConstants.REALM_ID).asLong();
    }
}
