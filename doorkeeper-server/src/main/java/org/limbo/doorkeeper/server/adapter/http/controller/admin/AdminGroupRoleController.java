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
import org.limbo.doorkeeper.api.model.vo.ResponseVO;
import org.limbo.doorkeeper.api.model.param.batch.GroupRoleBatchUpdateParam;
import org.limbo.doorkeeper.api.model.vo.GroupRoleVO;
import org.limbo.doorkeeper.server.adapter.http.controller.BaseController;
import org.limbo.doorkeeper.server.service.GroupRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/5 4:46 下午
 */
@Tag(name = "用户组角色")
@Slf4j
@RestController
@RequestMapping("/api/admin/realm/{realmId}/group")
public class AdminGroupRoleController extends BaseController {

    @Autowired
    private GroupRoleService groupRoleService;

    @Operation(summary = "查询用户组角色列表")
    @GetMapping("/{groupId}/role")
    public ResponseVO<List<GroupRoleVO>> listRole(@Validated @NotNull(message = "未提交用户组ID") @PathVariable("groupId") Long groupId) {
        return ResponseVO.success(groupRoleService.list(getRealmId(), groupId));
    }

    @Operation(summary = "批量修改用户组角色")
    @PostMapping("/{groupId}/role/batch")
    public ResponseVO<Void> batchRole(@Validated @NotNull(message = "未提交用户组ID") @PathVariable("groupId") Long groupId,
                                      @RequestBody @Validated GroupRoleBatchUpdateParam param) {
        groupRoleService.batchUpdate(groupId, param);
        return ResponseVO.success();
    }

}
