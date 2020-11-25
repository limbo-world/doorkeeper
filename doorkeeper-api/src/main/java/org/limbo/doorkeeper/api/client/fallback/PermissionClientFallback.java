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
import org.limbo.doorkeeper.api.client.PermissionClient;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.PermissionAddParam;
import org.limbo.doorkeeper.api.model.param.PermissionBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.PermissionQueryParam;
import org.limbo.doorkeeper.api.model.param.PermissionUpdateParam;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/23 4:54 PM
 */
@Slf4j
@Component
public class PermissionClientFallback extends Fallback implements FallbackFactory<PermissionClient> {
    @Override
    public PermissionClient create(Throwable throwable) {
        log.error("服务调用失败", throwable);
        return new PermissionClient() {
            @Override
            public Response<PermissionVO> add(PermissionAddParam param) {
                return serviceUnavailable();
            }

            @Override
            public Response<Integer> update(Long permissionId, PermissionUpdateParam param) {
                return serviceUnavailable();
            }

            @Override
            public Response<Boolean> batchUpdate(PermissionBatchUpdateParam param) {
                return serviceUnavailable();
            }

            @Override
            public Response<Boolean> delete(List<Long> permissionIds) {
                return serviceUnavailable();
            }

            @Override
            public Response<List<PermissionVO>> list() {
                return serviceUnavailable();
            }

            @Override
            public Response<Page<PermissionVO>> query(PermissionQueryParam param) {
                return serviceUnavailable();
            }
        };
    }
}
