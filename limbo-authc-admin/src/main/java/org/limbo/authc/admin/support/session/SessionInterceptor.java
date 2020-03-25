/*
 *  Copyright 2020-2024 Limbo Team (https://github.com/LimboHome).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.limbo.authc.admin.support.session;

import org.limbo.authc.admin.support.session.validate.SessionValidator;
import org.limbo.authc.admin.support.session.validate.SessionValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.limbo.authc.admin.support.autoconfig.AuthcProperties;
import org.limbo.authc.session.SessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthcProperties authcProperties;

    @Autowired
    private RedisSessionDAO redisSessionDAO;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 验证会话存在
        String sessionId = request.getHeader(authcProperties.getSession().getHeaderName());
        AdminSession adminSession = redisSessionDAO.readSessionMayNull(sessionId);
        if (adminSession == null) {
            throw new SessionException("无有效会话");
        }

        /*
           一、前端数据加密
            1. 加密内容
                ts: 当前时间戳
                key: 公钥
                url: window.location.pathname
                sessionId: 会话id
                对于上面参数拼接得到如下字符串作为加密内容；
                    ts=xxx&key=xxx&url=xxx&sessionId=xxx
            2. 执行加密
                得到待加密字符串后，使用登录时下发的公钥对字符串进行加密，加密使用RSA算法；
            3. 签名请求
                发起请求时，在请求中附加一个header，header内容为第3步中加密后得到的字符串，header名为"Limbo-Authc-Sign"，
                header名可以通过authc.sign.header-name配置；
                同时，发起请求时需要附加sessionId header，header内容为sessionId，header名为"Limbo-Authc-Session"，
                header名可以通过authc.session.header-name配置

           二、后端解密验证
            1. 解密文本
                根据sessionId header得到会话对象，会话中存储了私钥，使用私钥对数据进行解密，得到明文文本；
            2. 分割数据
                根据配置的separator，分割得到请求参数和附加参数；
            3. 校验签名
                a. 校验sessionId是否正确
                b. 校验公钥是否正确
                c. 校验时间戳与当前实际是否在允许误差内
                d. 校验解密得到的请求参数与实际请求参数是否相同；
         */


        // 验证签名
        SessionValidator v = SessionValidatorFactory.create(authcProperties, request);
        if (!v.validate(adminSession, request)) {
            throw new SessionException("签名校验失败");
        }

        // 会话正常，则更新会话过期时间，异步执行
        CompletableFuture.runAsync(() -> redisSessionDAO.touchSession(sessionId));
        return true;
    }
}