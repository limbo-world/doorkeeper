/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   	http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.limbo.doorkeeper.api.client;

import org.limbo.doorkeeper.api.client.fallback.AccountRoleClientFallback;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.AccountRoleAddParam;
import org.limbo.doorkeeper.api.model.param.AccountRoleQueryParam;
import org.limbo.doorkeeper.api.model.vo.AccountRoleVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 * @author Devil
 * @date 2020/11/27 10:21 AM
 */
@FeignClient(name = "doorkeeper-server", path = "/account-role", contextId = "accountRoleClient",
        fallbackFactory = AccountRoleClientFallback.class)
public interface AccountRoleClient {

    @GetMapping
    Response<List<AccountRoleVO>> list(@SpringQueryMap AccountRoleQueryParam param);

    @PostMapping
    Response<Boolean> batchSave(@RequestBody List<AccountRoleAddParam> params);


    @DeleteMapping
    Response<Integer> batchDelete(@RequestBody List<Long> accountRoleIds);

}
