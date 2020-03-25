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

package org.limbo.authc.admin.support.authc;

import org.limbo.authc.api.interfaces.beans.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 授权失败处理器。
 * 可以实现自定义处理的逻辑，即使授权校验失败也可以通过返回AuthcFailOperation.CONTINUE来实现继续访问；
 * 不提供改handler的情况下将阻止请求继续执行，并返回Http状态码401
 *
 * @author Brozen
 * @date 2020/3/10 10:51 AM
 * @email brozen@qq.com
 */
public interface AuthcFailHandler {

    /**
     * 授权校验失败时的处理
     * @param request           Http请求
     * @param response          Http响应
     * @param authcResponse     授权接口的返回信息
     * @return @see{org.limbo.authc.api.interfaces.support.web.AuthcFailHandler.AuthcFailOperation}
     */
    AuthcFailOperation handle(HttpServletRequest request, HttpServletResponse response, Response<?> authcResponse);


    enum AuthcFailOperation {

        /**
         * 忽略授权校验失败信息，记录向下执行
         */
        CONTINUE,

        /**
         * 拒绝向下执行，直接返回
         */
        REFUSE,
    }

}
