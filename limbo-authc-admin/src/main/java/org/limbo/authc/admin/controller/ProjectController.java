/*
 *  Copyright 2020-2024 Limbo Team (https://github.com/LimboHome).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.limbo.authc.admin.controller;

import org.limbo.authc.admin.beans.po.AdminProjectPO;
import org.limbo.authc.admin.dubbo.consumers.ProjectDubboConsumer;
import org.limbo.authc.admin.service.AdminProjectService;
import org.limbo.authc.admin.support.blog.BLog;
import org.limbo.authc.admin.support.blog.BLogType;
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.ProjectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/9 2:32 PM
 * @email brozen@qq.com
 */
@RestController
@RequestMapping("/project")
public class ProjectController extends BaseController {

    @Autowired
    private ProjectDubboConsumer projectDubboConsumer;

    @Autowired
    private AdminProjectService adminProjectService;

    @PostMapping
    @BLog(name = "添加项目", type = BLogType.CREATE)
    public Response<ProjectVO> add(@RequestBody ProjectVO.AddParam param) {
        return adminProjectService.add(param);
    }

    @GetMapping
    public Response<List<ProjectVO>> list() {
        return projectDubboConsumer.list();
    }

    @GetMapping("/query")
    public Response<Page<ProjectVO>> query(ProjectVO.QueryParam param) {
        return projectDubboConsumer.query(param);
    }

    @GetMapping("/{projectId}")
    public Response<ProjectVO> get(@Validated @PathVariable("projectId") @NotNull(message = "项目不存在") Long projectId) {
        return projectDubboConsumer.get(new ProjectVO.GetParam(projectId));
    }

    @GetMapping("/{projectId}/secret")
    @BLog(expression = "'查看项目Secret '.concat(#arg0)", type = BLogType.RETRIEVE)
    public Response<String> getSecret(@Validated @PathVariable("projectId") @NotNull(message = "项目不存在") Long projectId) {
        return projectDubboConsumer.getSecret(new ProjectVO.GetParam(projectId));
    }

    @PutMapping("/{projectId}")
    @BLog(expression = "'更新项目 '.concat(#arg1)", type = BLogType.UPDATE)
    public Response<AdminProjectPO> update(
            @RequestBody ProjectVO.UpdateParam param,
            @Validated @PathVariable("projectId") @NotNull(message = "项目不存在") Long projectId
    ) {
        param.setProjectId(projectId);
        return adminProjectService.update(param);
    }

    @DeleteMapping("/{projectId}")
    @BLog(expression = "'删除项目 '.concat(#arg0)", type = BLogType.DELETE)
    public Response<ProjectVO> delete(@Validated @PathVariable("projectId") @NotNull(message = "项目不存在") Long projectId) {
        return adminProjectService.delete(projectId);
    }

}
