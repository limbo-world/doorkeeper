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
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.client.ClientAddParam;
import org.limbo.doorkeeper.api.model.param.client.ClientQueryParam;
import org.limbo.doorkeeper.api.model.param.client.ClientUpdateParam;
import org.limbo.doorkeeper.api.model.vo.ClientVO;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.limbo.doorkeeper.server.controller.BaseController;
import org.limbo.doorkeeper.server.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/4 2:55 下午
 */
@Tag(name = "委托方")
@Slf4j
@RestController
@RequestMapping("/admin/client")
public class AdminClientController extends BaseController {

    @Autowired
    private ClientService clientService;

    @Operation(summary = "新建委托方")
    @PostMapping
    public Response<ClientVO> add(@RequestBody @Validated ClientAddParam param) {
        return Response.success(clientService.add(getUserId(), param));
    }

    @Operation(summary = "查询账户拥有的委托方")
    @GetMapping
    public Response<List<ClientVO>> userClients(@Validated ClientQueryParam param) {
        return Response.success(clientService.userClients(getUserId(), param));
    }

    @Operation(summary = "查询委托方")
    @GetMapping("/{clientId}")
    public Response<ClientVO> get(@Validated @NotNull(message = "未提交委托方ID") @PathVariable("clientId") Long clientId) {
        return Response.success(clientService.get(clientId));
    }

    @Operation(summary = "更新委托方")
    @PutMapping("/{clientId}")
    public Response<RoleVO> update(@Validated @NotNull(message = "未提交委托方ID") @PathVariable("clientId") Long clientId,
                                   @Validated @RequestBody ClientUpdateParam param) {
        clientService.update(clientId, param);
        return Response.success();
    }

}
