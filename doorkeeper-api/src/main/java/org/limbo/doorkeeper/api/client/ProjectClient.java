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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.ProjectAddParam;
import org.limbo.doorkeeper.api.model.param.ProjectQueryParam;
import org.limbo.doorkeeper.api.model.param.ProjectUpdateParam;
import org.limbo.doorkeeper.api.model.vo.ProjectVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuqingtong
 * @date 2020/11/20 17:47
 */
@Tag(name = "项目", description = "只有管理端可以调用")
@FeignClient(name = "doorkeeper-server", path = "/project", contextId = "projectClient")
interface ProjectClient {

    @PostMapping
    @Operation(summary = "新增项目")
    Response<ProjectVO> addProject(@RequestBody ProjectAddParam project);


    @DeleteMapping("/{projectId}")
    @Operation(summary = "删除项目")
    Response<ProjectVO> deleteProject(@PathVariable("projectId") Long projectId);

    @GetMapping("/{projectId}/secret")
    @Operation(summary = "获取项目秘钥")
    Response<String> getProjectSecret(@PathVariable("projectId") Long projectId);

    @PutMapping("/{projectId}")
    @Operation(summary = "更新项目")
    Response<Integer> updateProject(@PathVariable("projectId") Long projectId,
                                    @RequestBody ProjectUpdateParam project);

    @GetMapping
    @Operation(summary = "分页获取项目列表")
    Response<Page<ProjectVO>> getProjects(@SpringQueryMap ProjectQueryParam param);
    
}
