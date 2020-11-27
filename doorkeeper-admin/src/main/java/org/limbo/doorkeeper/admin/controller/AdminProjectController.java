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

import org.limbo.doorkeeper.admin.entity.AdminProject;
import org.limbo.doorkeeper.admin.model.param.AdminProjectQueryParam;
import org.limbo.doorkeeper.admin.model.param.AdminProjectUpdateParam;
import org.limbo.doorkeeper.admin.service.AdminProjectService;
import org.limbo.doorkeeper.api.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Devil
 * @date 2020/11/24 2:34 PM
 */
@RestController
@RequestMapping("/admin-project")
public class AdminProjectController extends BaseController {

    @Autowired
    private AdminProjectService adminProjectService;

    /**
     * 获取管理员拥有哪些未删除的项目
     */
    @GetMapping
    public Response<List<AdminProject>> list(AdminProjectQueryParam param) {
        return Response.ok(adminProjectService.list(param));
    }

    /**
     * 更新账户的项目
     */
    @PutMapping
    public Response<Boolean> update(@RequestBody AdminProjectUpdateParam param) {
        adminProjectService.update(param);
        return Response.ok(true);
    }

}
