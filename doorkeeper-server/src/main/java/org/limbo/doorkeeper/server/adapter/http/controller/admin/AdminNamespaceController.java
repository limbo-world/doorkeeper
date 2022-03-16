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
import org.limbo.doorkeeper.api.dto.param.add.NamespaceAddParam;
import org.limbo.doorkeeper.api.dto.param.update.ClientUpdateParam;
import org.limbo.doorkeeper.api.dto.vo.NamespaceVO;
import org.limbo.doorkeeper.api.dto.vo.ResponseVO;
import org.limbo.doorkeeper.api.dto.vo.RoleVO;
import org.limbo.doorkeeper.application.service.DoorkeeperService;
import org.limbo.doorkeeper.application.service.NamespaceAppService;
import org.limbo.doorkeeper.server.adapter.http.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Devil
 * @since 2021/1/4 2:55 下午
 */
@Tag(name = "命名空间")
@Slf4j
@RestController
@RequestMapping("/api/doorkeeper/v1/admin/realm/{realmId}/namespace")
public class AdminNamespaceController extends BaseController {

    @Autowired
    private NamespaceAppService namespaceAppService;

    @Autowired
    private DoorkeeperService doorkeeperService;

    @Operation(summary = "新建命令空间")
    @PostMapping
    public ResponseVO<NamespaceVO> add(@RequestBody @Validated NamespaceAddParam param) {
        return ResponseVO.success(doorkeeperService.addNamespace(getRealmId(), getUser().getUserId(), param));
    }

    @Operation(summary = "命名空间列表")
    @GetMapping
    public ResponseVO<List<NamespaceVO>> namespaces() {
        return ResponseVO.success(namespaceAppService.list(getRealmId()));
    }

    @Operation(summary = "获取命名空间")
    @GetMapping("/{namespaceId}")
    public ResponseVO<NamespaceVO> get(@Validated @NotNull(message = "namespaceId is null") @PathVariable("namespaceId") Long namespaceId) {
        return ResponseVO.success(namespaceAppService.get(namespaceId));
    }

    @Operation(summary = "更新命名空间")
    @PutMapping("/{namespaceId}")
    public ResponseVO<RoleVO> update(@Validated @NotNull(message = "namespaceId is null") @PathVariable("namespaceId") Long namespaceId,
                                     @Validated @RequestBody ClientUpdateParam param) {
        namespaceAppService.update(namespaceId, param);
        return ResponseVO.success();
    }

}
