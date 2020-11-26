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

import org.limbo.doorkeeper.api.client.fallback.ProjectClientFallback;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.ProjectAddParam;
import org.limbo.doorkeeper.api.model.param.ProjectQueryParam;
import org.limbo.doorkeeper.api.model.param.ProjectUpdateParam;
import org.limbo.doorkeeper.api.model.vo.ProjectVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuqingtong
 * @date 2020/11/20 17:47
 */
@FeignClient(name = "doorkeeper-server", path = "/project", contextId = "projectClient", fallbackFactory = ProjectClientFallback.class)
public interface ProjectClient {

    @PostMapping
    Response<ProjectVO> addProject(@RequestBody ProjectAddParam project);

    @GetMapping
    Response<List<ProjectVO>> getAll();

    @GetMapping("/query")
    Response<Page<ProjectVO>> getProjects(@SpringQueryMap ProjectQueryParam param);

    @GetMapping("/{projectId}/secret")
    Response<String> getProjectSecret(@PathVariable("projectId") Long projectId);

    @GetMapping("/{projectId}")
    Response<ProjectVO> getProject(@PathVariable("projectId") Long projectId);

    @PutMapping("/{projectId}")
    Response<Integer> updateProject(@PathVariable("projectId") Long projectId,
                                    @RequestBody ProjectUpdateParam project);
    
}
