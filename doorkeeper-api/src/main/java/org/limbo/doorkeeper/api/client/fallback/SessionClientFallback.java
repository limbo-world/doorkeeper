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
import org.limbo.doorkeeper.api.client.SessionClient;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.RepasswordParam;
import org.limbo.doorkeeper.api.model.vo.AccountGrantVO;
import org.limbo.doorkeeper.api.model.vo.ProjectAccountVO;
import org.limbo.doorkeeper.api.model.vo.SessionVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/23 4:55 PM
 */
@Slf4j
@Component
public class SessionClientFallback extends Fallback implements FallbackFactory<SessionClient> {
    @Override
    public SessionClient create(Throwable throwable) {
        log.error("服务调用失败", throwable);
        return new SessionClient() {
            @Override
            public Response<SessionVO> session() {
                return serviceUnavailable();
            }

            @Override
            public Response<SessionVO> switchProject(Long projectId) {
                return serviceUnavailable();
            }

            @Override
            public Response<AccountGrantVO> getGrantInfo() {
                return serviceUnavailable();
            }

            @Override
            public Response<List<ProjectAccountVO>> project() {
                return serviceUnavailable();
            }

            @Override
            public Response<Boolean> repassword(RepasswordParam param) {
                return serviceUnavailable();
            }

            @Override
            public Response<Boolean> logout() {
                return serviceUnavailable();
            }
        };
    }
}
