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

package org.limbo.doorkeeper.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.ProjectAddParam;
import org.limbo.doorkeeper.api.model.param.ProjectQueryParam;
import org.limbo.doorkeeper.api.model.param.ProjectUpdateParam;
import org.limbo.doorkeeper.api.model.vo.ProjectVO;
import org.limbo.doorkeeper.server.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @author Devil
 * @date 2020/11/20 2:18 PM
 */
@Tag(name = "项目", description = "只有管理端可以调用")
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;


    @PostMapping
    @Operation(summary = "新增项目")
    public Response<ProjectVO> addProject(@RequestBody ProjectAddParam project) {
        return Response.ok(projectService.addProject(project, false));
    }

    @GetMapping("/{projectId}/secret")
    @Operation(summary = "获取项目秘钥")
    public Response<String> getProjectSecret(@Validated @NotNull(message = "项目不存在") @PathVariable("projectId") Long projectId) {
        return Response.ok(projectService.getSecret(projectId));
    }

    @PutMapping("/{projectId}")
    @Operation(summary = "更新项目")
    public Response<Integer> updateProject(@Validated @NotNull(message = "项目不存在") @PathVariable("projectId") Long projectId,
                                           @RequestBody ProjectUpdateParam project) {
        project.setProjectId(projectId);
        return Response.ok(projectService.updateProject(project));
    }

    @GetMapping
    @Operation(summary = "分页获取项目列表")
    public Response<Page<ProjectVO>> getProjects(ProjectQueryParam param) {
        return Response.ok(projectService.queryProjectPage(param));
    }

}
