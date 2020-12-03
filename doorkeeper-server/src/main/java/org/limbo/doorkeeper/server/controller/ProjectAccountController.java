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

package org.limbo.doorkeeper.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.ProjectAccountQueryParam;
import org.limbo.doorkeeper.api.model.vo.ProjectAccountVO;
import org.limbo.doorkeeper.server.service.ProjectAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Devil
 * @Date 2020/12/3 3:29 下午
 */
@Tag(name = "项目账户关系")
@RestController
@RequestMapping("/project-account")
public class ProjectAccountController extends BaseController {

    @Autowired
    private ProjectAccountService projectAccountService;

    @GetMapping("/query")
    @Operation(summary = "分页查询项目账户关系")
    public Response<Page<ProjectAccountVO>> query(ProjectAccountQueryParam param) {
        return Response.ok(projectAccountService.page(param));
    }

}
