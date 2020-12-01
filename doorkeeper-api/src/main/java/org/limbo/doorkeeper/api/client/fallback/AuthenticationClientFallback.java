/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.limbo.doorkeeper.api.client.fallback;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.client.AuthenticationClient;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.AccountGrantInfoGetParam;
import org.limbo.doorkeeper.api.model.param.ApiCheckParam;
import org.limbo.doorkeeper.api.model.param.PermissionCheckParam;
import org.limbo.doorkeeper.api.model.param.RoleCheckParam;
import org.limbo.doorkeeper.api.model.vo.AccountGrantVO;
import org.springframework.stereotype.Component;

/**
 * @author Devil
 * @date 2020/11/23 4:55 PM
 */
@Slf4j
@Component
public class AuthenticationClientFallback extends Fallback implements FallbackFactory<AuthenticationClient> {
    @Override
    public AuthenticationClient create(Throwable throwable) {
        log.error("服务调用失败", throwable);
        return new AuthenticationClient() {
            @Override
            public Response<AccountGrantVO> grantInfo(AccountGrantInfoGetParam param) {
                return serviceUnavailable();
            }

            @Override
            public Response<Boolean> apiCheck(ApiCheckParam param) {
                return serviceUnavailable();
            }

            @Override
            public Response<Boolean> permissionCheck(PermissionCheckParam param) {
                return serviceUnavailable();
            }

            @Override
            public Response<Boolean> roleCheck(RoleCheckParam param) {
                return serviceUnavailable();
            }
        };
    }
}
