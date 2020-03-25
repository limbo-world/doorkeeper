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

import lombok.extern.slf4j.Slf4j;
import org.limbo.authc.api.interfaces.apis.AuthorizationApi;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.AuthorizationVO;
import org.limbo.authc.api.interfaces.exception.AuthcException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 基于SpringMVC实现的授权校验拦截器
 *
 * @author devil
 * @date 2020/3/9
 */
@Slf4j
public class AuthcInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthcInfoSupplier authcInfoSupplier;

    @Autowired
    private AuthcFailHandler failHandler;

    @Autowired
    private AuthorizationApi authorizationApi;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Objects.requireNonNull(authcInfoSupplier);
        Objects.requireNonNull(authorizationApi);

        AuthcInfo authcInfo = this.authcInfoSupplier.get(request, response);
        try {
            AuthorizationVO.PermissionCheckParam param = new AuthorizationVO.PermissionCheckParam();
            param.setAccountId(authcInfo.getAccountId());
            param.setHttpMethod(request.getMethod());
            param.setPath(request.getServletPath());

            Response<Boolean> resp = authorizationApi.hasPermission(param);

            if (resp.ok() && resp.getData()) {
                return true;
            }

            // 根据返回结果和请求判断是否向下执行
            if (failHandler.handle(request, response, resp) ==  AuthcFailHandler.AuthcFailOperation.CONTINUE) {
                return true;
            }

            throw new AuthcException();

        } catch (AuthcException e) {
            throw e;
        } catch (Exception e) {
            log.error("授权认证失败！", e);
            throw new IllegalStateException("授权认证拦截器发生异常情况！", e);
        }
    }

}
