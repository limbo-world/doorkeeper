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
 * @author Brozen
 * @date 2020/3/11 10:00 AM
 * @email brozen@qq.com
 *
 * 对于请求参数进行签名认证 如GET、POST会通过 request.getParameter request.getBody 分别获取请求参数
 */
@Slf4j
public abstract class ParamSessionValidator implements SessionValidator {

    @Setter
    protected AuthcProperties authcProperties;

    public ParamSessionValidator(AuthcProperties authcProperties) {
        this.authcProperties = authcProperties;
    }

    @Override
    public boolean validate(AbstractSession session, HttpServletRequest request) {
        try {
            // 1. 解密文本
            SecurityDigest securityDigest = session.getSecurityDigest();
            String content = getSignedContent();
            byte[] decryptedBytes = RSAUtils.decryptByPrivateKey(content, securityDigest.getPrivateKey());
            String decryptedContent = new String(decryptedBytes);

            // 2. 分割数据
            String separator = authcProperties.getSign().getSeparator();
            String[] params = decryptedContent.split(separator);
            if (params.length != 2) {
                // 分割出的不是两段数据，说明签名有误
                return false;
            }
            String decryptedRequestParam = params[0];
            SignAttachment signAttachment = SignAttachment.parse(params[1]);

            // 3. 校验签名
            return StringUtils.equals(session.getSessionId(), signAttachment.sessionId)
                    && StringUtils.equals(securityDigest.getPublicKey(), signAttachment.getKey())
                    && Math.abs(System.currentTimeMillis() - signAttachment.getTs()) < authcProperties.getSign().getTimestampMistake()
                    && validateParam(decryptedRequestParam, getRequestParam());

        } catch (GeneralSecurityException e) {
            // 抛出解密异常，基本不可能是不支持RSA算法，应该是签名有误
            log.debug("解密会话失败：", e);
            return false;
        } catch (IllegalArgumentException e) {
            log.debug("校验会话失败，参数错误！", e);
            return false;
        }
    }

    /**
     * 校验签名中加密的参数与request中的参数是否一致
     * @param decryptedRequestParam     签名中的参数
     * @param requestParam              请求中的参数
     * @return
     */
    protected abstract boolean validateParam(String decryptedRequestParam, String requestParam);

    /**
     * 获取加密的签名
     */
    protected abstract String getSignedContent();

    /**
     * 获取request中的参数，明文参数
     */
    protected abstract String getRequestParam();


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
         * 公钥
         */
        private String key;

        private void validate() {
            if (ts == null) {
                throw new IllegalArgumentException("附加参数缺失: ts");
            }
            if (StringUtils.isBlank(sessionId)) {
                throw new IllegalArgumentException("附加参数缺失: sessionId");
            }
            if (StringUtils.isBlank(key)) {
                throw new IllegalArgumentException("附加参数缺失: key");
            }
        }

        static SignAttachment parse(String param) {
            String[] params = param.split("&");
            if (params.length != 3) {
                throw new IllegalArgumentException("附加参数个数有误：需要3个，发现 " + params.length + " 个，param=" + param);
            }

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

                    case "key":
                        attachment.key = kv[1];
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
