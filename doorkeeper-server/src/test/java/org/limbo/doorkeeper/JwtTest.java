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

package org.limbo.doorkeeper;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.limbo.doorkeeper.server.infrastructure.utils.JacksonUtil;

import java.util.Date;

/**
 * @author Devil
 * @since 2021/1/15 4:25 下午
 */
public class JwtTest {

    @Test(expected = SignatureVerificationException.class)
    public void test(){
        String token = JWT.create()
                .withExpiresAt(DateUtils.addHours(new Date(), 1))  //设置过期时间
                .withSubject("sss")
                .withAudience("user1") //设置接受方信息，一般时登录用户
                .sign(Algorithm.HMAC256("2222"));

        String userId = JWT.decode(token).getAudience().get(0);
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("111111")).build();
        DecodedJWT verify = jwtVerifier.verify(token);

        verify = jwtVerifier.verify("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzc3MiLCJhdWQiOiJ1c2VyMSIsImV4cCx6MTYxMDcxMTU2NH0.-rftBST3A8FOTnGhc7mKV5KK9OkG1pm74IPyuMZkegI");
    }

    @Test(expected = SignatureVerificationException.class)
    public void testOther() {
        String token = JWT.create()
                .withExpiresAt(DateUtils.addHours(new Date(), 1))  //设置过期时间
                .withSubject("sss")
                .withAudience("user1") //设置接受方信息，一般时登录用户
                .sign(Algorithm.HMAC256("2222"));

        String userId = JWT.decode(token).getAudience().get(0);
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("111111")).build();
        DecodedJWT verify = jwtVerifier.verify(token);

        System.out.println(JacksonUtil.toJSONString(verify));
    }

}
