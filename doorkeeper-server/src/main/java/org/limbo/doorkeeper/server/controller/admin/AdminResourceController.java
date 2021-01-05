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

package org.limbo.doorkeeper.server.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.ResourceAddParam;
import org.limbo.doorkeeper.api.model.param.ResourceQueryParam;
import org.limbo.doorkeeper.api.model.param.ResourceUpdateParam;
import org.limbo.doorkeeper.api.model.vo.ResourceVO;
import org.limbo.doorkeeper.server.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @author Devil
 * @date 2021/1/5 4:46 下午
 */
@Tag(name = "资源")
@Slf4j
@RestController
@RequestMapping("/admin/resource")
public class AdminResourceController {

    @Autowired
    private ResourceService resourceService;

    @Operation(summary = "新建资源")
    @PostMapping
    public Response<Void> add(@RequestBody @Validated ResourceAddParam param) {
        resourceService.add(param);
        return Response.success();
    }

    @Operation(summary = "分页查询资源")
    @GetMapping
    public Response<Page<ResourceVO>> page(@Validated ResourceQueryParam param) {
        return Response.success(resourceService.page(param));
    }

    @Operation(summary = "查询资源")
    @GetMapping("/{resourceId}")
    public Response<ResourceVO> get(@Validated @NotNull(message = "未提交资源ID") @PathVariable("resourceId") Long resourceId) {
        return Response.success(resourceService.get(resourceId));
    }

    @Operation(summary = "更新资源")
    @PutMapping("/{resourceId}")
    public Response<Void> update(@Validated @NotNull(message = "未提交资源ID") @PathVariable("resourceId") Long resourceId,
                                   @Validated @RequestBody ResourceUpdateParam param) {
        resourceService.update(resourceId, param);
        return Response.success();
    }
}
