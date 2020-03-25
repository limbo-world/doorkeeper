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

package org.limbo.authc.admin.support.session.validate;

import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.limbo.authc.admin.support.autoconfig.AuthcProperties;
import org.limbo.authc.session.AbstractSession;
import org.limbo.authc.session.SecurityDigest;
import org.limbo.authc.session.utils.RSAUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.GeneralSecurityException;

/**
 * @author devil
 * @date 2020/3/11
 * 从请求头中获取签名信息进行校验
 */
@Slf4j
public class HeaderSessionValidator implements SessionValidator {
    @Setter
    protected AuthcProperties authcProperties;

    public HeaderSessionValidator(AuthcProperties authcProperties) {
        this.authcProperties = authcProperties;
    }

    @Override
    public boolean validate(AbstractSession session, HttpServletRequest request) {

        try {
            // 1. 解密文本
            SecurityDigest securityDigest = session.getSecurityDigest();
            String sign = request.getHeader(authcProperties.getSign().getHeaderName());
            byte[] decryptedBytes = RSAUtils.decryptByPrivateKey(sign, securityDigest.getPrivateKey());
            String decryptedSign = new String(decryptedBytes);

            // 2. 封装参数
            SignAttachment signAttachment = SignAttachment.parse(decryptedSign);

            // 3. 校验签名
            return StringUtils.equals(session.getSessionId(), signAttachment.sessionId)
                    && Math.abs(System.currentTimeMillis() - signAttachment.getTs()) < authcProperties.getSign().getTimestampMistake()
                    && StringUtils.equals(request.getServletPath(), signAttachment.getUrl());
        } catch (GeneralSecurityException e) {
            // 抛出解密异常，基本不可能是不支持RSA算法，应该是签名有误
            log.debug("解密会话失败：", e);
        } catch (IllegalArgumentException e) {
            log.debug("校验会话失败，参数错误！", e);
        }
        return false;
    }

    /**
     * 请求签名时的附加参数
     */
    @Data
    public static class SignAttachment {
        /**
         * 发起请求时的时间戳
         */
        private Long ts;

        /**
         * 会话ID
         */
        private String sessionId;
        /**
         * 请求路径
         */
        private String url;

        private void validate() {
            if (ts == null) {
                throw new IllegalArgumentException("附加参数缺失: ts");
            }
            if (StringUtils.isBlank(sessionId)) {
                throw new IllegalArgumentException("附加参数缺失: sessionId");
            }
            if (StringUtils.isBlank(url)) {
                throw new IllegalArgumentException("附加参数缺失: url");
            }
        }

        static SignAttachment parse(String param) {
            String[] params = param.split("&");

            SignAttachment attachment = new SignAttachment();
            for (String p : params) {
                String[] kv = p.split("=");
                if (kv.length != 2) {
                    throw new IllegalArgumentException("附加参数有误：" + p);
                }

                switch (kv[0]) {
                    case "ts":
                        attachment.ts = Long.parseLong(kv[1]);
                        break;

                    case "sessionId":
                        attachment.sessionId = kv[1];
                        break;

                    case "url":
                        attachment.url = kv[1];
                        break;

                    default:
                        throw new IllegalArgumentException("未知的附加参数：" + p);
                }
            }

            attachment.validate();
            return attachment;
        }
    }
}
