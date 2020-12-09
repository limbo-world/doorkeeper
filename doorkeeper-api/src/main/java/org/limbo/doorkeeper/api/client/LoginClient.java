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

import org.limbo.doorkeeper.api.client.fallback.LoginClientFallback;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.LoginParam;
import org.limbo.doorkeeper.api.model.vo.SessionVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author devil
 * @date 2020/3/11
 */
@FeignClient(name = "doorkeeper-server", path = "/login", contextId = "loginClient",
        fallbackFactory = LoginClientFallback.class)
public interface LoginClient {

    @PostMapping
    Response<SessionVO> login(@RequestBody LoginParam param);
}