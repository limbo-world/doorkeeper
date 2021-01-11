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
import org.limbo.doorkeeper.server.controller.BaseController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Devil
 * @date 2021/1/10 10:11 下午
 */
@Tag(name = "管理")
@Slf4j
@RestController
@RequestMapping("/admin/manager")
public class AdminManagerController extends BaseController {

    @Operation(summary = "转让域拥有者")
    @PutMapping("/realm-owner")
    public Response<Void> transferRealmOwner() {
        return null;
    }

    @Operation(summary = "转让委托方拥有者")
    @PutMapping("/client-owner")
    public Response<Void> transferClientOwner() {
        return null;
    }

    @Operation(summary = "新建域管理员")
    @PostMapping("/realm-admin")
    public Response<Void> addRealmAdmin() {
        return null;
    }

    @Operation(summary = "新建委托方管理员")
    @PostMapping("/client-admin")
    public Response<Void> addClientAdmin() {
        return null;
    }

}
