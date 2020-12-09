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

import org.limbo.doorkeeper.api.client.fallback.AccountClientFallback;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.RepasswordParam;
import org.limbo.doorkeeper.api.model.vo.AccountGrantVO;
import org.limbo.doorkeeper.api.model.vo.ProjectAccountVO;
import org.limbo.doorkeeper.api.model.vo.SessionVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author devil
 * @date 2020/3/11
 */
@FeignClient(name = "doorkeeper-server", path = "/session", contextId = "sessionClient", fallbackFactory = AccountClientFallback.class)
public interface SessionClient {

    @GetMapping
    Response<SessionVO> session();

    @PutMapping("/project/{projectId}")
    Response<SessionVO> switchProject(@PathVariable("projectId") Long projectId);

    @GetMapping("/grant-info")
    Response<AccountGrantVO> getGrantInfo();

    @GetMapping("/project")
    Response<List<ProjectAccountVO>> project();

    @PutMapping("/repassword")
    Response<Boolean> repassword(@RequestBody RepasswordParam param);

    @GetMapping("/logout")
    Response<Boolean> logout();

}
