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

package org.limbo.doorkeeper.api.client;

import org.limbo.doorkeeper.api.client.fallback.AccountClinentFallback;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.AccountAddParam;
import org.limbo.doorkeeper.api.model.param.AccountBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.AccountQueryParam;
import org.limbo.doorkeeper.api.model.vo.AccountVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author liuqingtong
 * @date 2020/11/20 17:45
 */
@FeignClient(name = "doorkeeper-server", path = "/account", contextId = "accountClient", fallbackFactory = AccountClinentFallback.class)
public interface AccountClient {

    @PostMapping
    Response<AccountVO> add(@RequestBody AccountAddParam project);

    @PutMapping
    Response<Integer> update(@RequestBody AccountBatchUpdateParam param);

    @GetMapping
    Response<Page<AccountVO>> page(@SpringQueryMap AccountQueryParam param);
    
}
