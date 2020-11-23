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
import org.limbo.doorkeeper.api.client.AccountClient;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.AccountAddParam;
import org.limbo.doorkeeper.api.model.param.AccountBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.AccountQueryParam;
import org.limbo.doorkeeper.api.model.vo.AccountVO;
import org.springframework.stereotype.Component;

/**
 * @author Devil
 * @date 2020/11/23 4:23 PM
 */
@Slf4j
@Component
public class AccountClinentFallback extends Fallback implements FallbackFactory<AccountClient> {
    @Override
    public AccountClient create(Throwable throwable) {
        log.error("服务调用失败", throwable);
        return new AccountClient() {
            @Override
            public Response<AccountVO> add(AccountAddParam project) {
                return serviceUnavailable();
            }

            @Override
            public Response<Integer> update(AccountBatchUpdateParam param) {
                return serviceUnavailable();
            }

            @Override
            public Response<Page<AccountVO>> page(AccountQueryParam param) {
                return serviceUnavailable();
            }
        };
    }
}
