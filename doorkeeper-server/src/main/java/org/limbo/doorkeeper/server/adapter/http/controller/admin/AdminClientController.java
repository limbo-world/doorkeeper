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
import org.limbo.doorkeeper.api.dto.vo.ResponseVO;
import org.limbo.doorkeeper.api.dto.param.add.ClientAddParam;
import org.limbo.doorkeeper.api.dto.param.query.ClientQueryParam;
import org.limbo.doorkeeper.api.dto.param.update.ClientUpdateParam;
import org.limbo.doorkeeper.api.dto.vo.ClientVO;
import org.limbo.doorkeeper.api.dto.vo.RoleVO;
import org.limbo.doorkeeper.server.adapter.http.controller.BaseController;
import org.limbo.doorkeeper.server.service.ClientService;
import org.limbo.doorkeeper.server.service.DoorkeeperService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Devil
 * @since 2021/1/4 2:55 下午
 */
@Tag(name = "委托方")
@Slf4j
@RestController
@RequestMapping("/api/doorkeeper/v1/admin/realm/{realmId}/client")
public class AdminClientController extends BaseController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private DoorkeeperService doorkeeperService;

    @Operation(summary = "新建委托方")
    @PostMapping
    public ResponseVO<ClientVO> add(@RequestBody @Validated ClientAddParam param) {
        return ResponseVO.success(doorkeeperService.addClient(getRealmId(), getUser().getUserId(), param));
    }

    @Operation(summary = "查询账户拥有的委托方")
    @GetMapping
    public ResponseVO<List<ClientVO>> userClients(@ParameterObject ClientQueryParam param) {
        return ResponseVO.success(doorkeeperService.userClients(getRealmId(), getUser().getUserId(), param));
    }

    @Operation(summary = "查询委托方")
    @GetMapping("/{clientId}")
    public ResponseVO<ClientVO> get(@Validated @NotNull(message = "未提交委托方ID") @PathVariable("clientId") Long clientId) {
        return ResponseVO.success(clientService.get(getRealmId(), clientId));
    }

    @Operation(summary = "更新委托方")
    @PutMapping("/{clientId}")
    public ResponseVO<RoleVO> update(@Validated @NotNull(message = "未提交委托方ID") @PathVariable("clientId") Long clientId,
                                     @Validated @RequestBody ClientUpdateParam param) {
        clientService.update(getRealmId(), clientId, param);
        return ResponseVO.success();
    }

}
