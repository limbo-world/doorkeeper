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

import org.limbo.doorkeeper.api.client.fallback.AuthenticationClientFallback;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.AccountGrantInfoGetParam;
import org.limbo.doorkeeper.api.model.param.ApiCheckParam;
import org.limbo.doorkeeper.api.model.param.PermissionCheckParam;
import org.limbo.doorkeeper.api.model.param.RoleCheckParam;
import org.limbo.doorkeeper.api.model.vo.AccountGrantVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Devil
 * @date 2020/11/30 10:23 AM
 */
@FeignClient(name = "doorkeeper-server", path = "/auth", contextId = "authenticationClient", fallbackFactory = AuthenticationClientFallback.class)
public interface AuthenticationClient {

    @GetMapping("/grant-info")
    Response<AccountGrantVO> grantInfo(@SpringQueryMap AccountGrantInfoGetParam param);

    @GetMapping("/check-api")
    Response<Boolean> apiCheck(@SpringQueryMap ApiCheckParam param);

    @GetMapping("/check-permission")
    Response<Boolean> permissionCheck(@SpringQueryMap PermissionCheckParam param);

    @GetMapping("/check-role")
    Response<Boolean> roleCheck(@SpringQueryMap RoleCheckParam param);

}
