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

package org.limbo.doorkeeper.server.adapter.http.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.dto.vo.PageVO;
import org.limbo.doorkeeper.api.dto.vo.ResponseVO;
import org.limbo.doorkeeper.api.dto.param.add.ResourceAddParam;
import org.limbo.doorkeeper.api.dto.param.batch.ResourceBatchUpdateParam;
import org.limbo.doorkeeper.api.dto.param.query.ResourceQueryParam;
import org.limbo.doorkeeper.api.dto.param.update.ResourceUpdateParam;
import org.limbo.doorkeeper.api.dto.vo.ResourceVO;
import org.limbo.doorkeeper.server.adapter.http.controller.BaseController;
import org.limbo.doorkeeper.server.application.service.ResourceService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @author Devil
 * @since 2021/1/5 4:46 下午
 */
@Tag(name = "资源")
@Slf4j
@RestController
@RequestMapping("/api/doorkeeper/v1/admin/realm/{realmId}/client/{clientId}/resource")
public class AdminResourceController extends BaseController {

    @Autowired
    private ResourceService resourceService;

    @Operation(summary = "新建资源")
    @PostMapping
    public ResponseVO<ResourceVO> add(@RequestBody @Validated ResourceAddParam param) {
        return ResponseVO.success(resourceService.add(getRealmId(), getClientId(), param));
    }

    @Operation(summary = "批量操作资源")
    @PostMapping("/batch")
    public ResponseVO<Void> batch(@RequestBody @Validated ResourceBatchUpdateParam param) {
        resourceService.batchUpdate(getRealmId(), getClientId(), param);
        return ResponseVO.success();
    }

    @Operation(summary = "分页查询资源")
    @GetMapping
    public ResponseVO<PageVO<ResourceVO>> page(@ParameterObject ResourceQueryParam param) {
        return ResponseVO.success(resourceService.page(getRealmId(), getClientId(), param));
    }

    @Operation(summary = "查询资源")
    @GetMapping("/{resourceId}")
    public ResponseVO<ResourceVO> get(@Validated @NotNull(message = "未提交资源ID") @PathVariable("resourceId") Long resourceId) {
        return ResponseVO.success(resourceService.get(getRealmId(), getClientId(), resourceId));
    }

    @Operation(summary = "更新资源")
    @PutMapping("/{resourceId}")
    public ResponseVO<Void> update(@Validated @NotNull(message = "未提交资源ID") @PathVariable("resourceId") Long resourceId,
                                   @Validated @RequestBody ResourceUpdateParam param) {
        resourceService.update(getRealmId(), getClientId(), resourceId, param);
        return ResponseVO.success();
    }
}
