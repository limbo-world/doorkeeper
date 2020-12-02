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

package org.limbo.doorkeeper.admin.controller;

import org.limbo.doorkeeper.api.client.ProjectClient;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.ProjectAddParam;
import org.limbo.doorkeeper.api.model.param.ProjectQueryParam;
import org.limbo.doorkeeper.api.model.param.ProjectUpdateParam;
import org.limbo.doorkeeper.api.model.vo.ProjectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/23 3:32 PM
 */
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectClient projectClient;

    @PostMapping
    public Response<ProjectVO> add(@RequestBody ProjectAddParam param) {
        return projectClient.addProject(param);
    }

    @PutMapping("/{projectId}")
    public Response<Integer> updateProject(@PathVariable("projectId") Long projectId,
                                    @RequestBody ProjectUpdateParam project) {
        return projectClient.updateProject(projectId, project);
    }

    @GetMapping
    public Response<List<ProjectVO>> getAll() {
        return projectClient.getAll();
    }

    @GetMapping("/query")
    public Response<Page<ProjectVO>> query(ProjectQueryParam param) {
        return projectClient.getProjects(param);
    }

    @GetMapping("/{projectId}/secret")
    public Response<String> getProjectSecret(@PathVariable("projectId") Long projectId){
        return projectClient.getProjectSecret(projectId);
    }

}
