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
import org.limbo.doorkeeper.api.model.param.role.RoleCombineBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.role.RoleCombineQueryParam;
import org.limbo.doorkeeper.api.model.vo.RoleCombineVO;
import org.limbo.doorkeeper.server.controller.BaseController;
import org.limbo.doorkeeper.server.service.RoleCombineService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/4 5:19 下午
 */
@Tag(name = "角色组合")
@Slf4j
@RestController
@RequestMapping("/api/admin/realm/{realmId}/role")
public class AdminRoleCombineController extends BaseController {

    @Autowired
    private RoleCombineService roleCombineService;

    @Operation(summary = "查询角色组合列表")
    @GetMapping("/{roleId}/role-combine")
    public Response<List<RoleCombineVO>> list(@Validated @NotNull(message = "未提交角色ID") @PathVariable("roleId") Long roleId,
                                              @ParameterObject RoleCombineQueryParam param) {
        return Response.success(roleCombineService.list(getRealmId(), roleId, param));
    }

    @Operation(summary = "批量修改角色组合")
    @PostMapping("/{roleId}/role-combine/batch")
    public Response<Void> batch(@Validated @NotNull(message = "未提交角色ID") @PathVariable("roleId") Long roleId,
                                               @RequestBody @Validated RoleCombineBatchUpdateParam param) {
        roleCombineService.batchUpdate(getRealmId(), roleId, param);
        return Response.success();
    }

}
