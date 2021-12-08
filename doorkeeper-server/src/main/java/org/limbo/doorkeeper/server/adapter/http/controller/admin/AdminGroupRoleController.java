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
import org.limbo.doorkeeper.api.dto.param.add.GroupRoleAddParam;
import org.limbo.doorkeeper.api.dto.param.delete.RoleBatchDeleteParam;
import org.limbo.doorkeeper.api.dto.param.update.GroupRoleUpdateParam;
import org.limbo.doorkeeper.api.dto.vo.GroupRoleVO;
import org.limbo.doorkeeper.api.dto.vo.ResponseVO;
import org.limbo.doorkeeper.server.adapter.http.controller.BaseController;
import org.limbo.doorkeeper.server.application.service.GroupRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Devil
 * @since 2021/1/5 4:46 下午
 */
@Tag(name = "用户组角色")
@Slf4j
@RestController
@RequestMapping("/api/doorkeeper/v1/admin/realm/{realmId}/group")
public class AdminGroupRoleController extends BaseController {

    @Autowired
    private GroupRoleService groupRoleService;

    @Operation(summary = "查询用户组角色列表")
    @GetMapping("/{groupId}/role")
    public ResponseVO<List<GroupRoleVO>> list(@Validated @NotNull(message = "groupId is null") @PathVariable("groupId") Long groupId) {
        return ResponseVO.success(groupRoleService.list(groupId));
    }

    @Operation(summary = "新增用户组角色")
    @PostMapping("/{groupId}/role")
    public ResponseVO<Void> add(@Validated @NotNull(message = "groupId is null") @PathVariable("groupId") Long groupId,
                                     @RequestBody @Validated GroupRoleAddParam param) {
        groupRoleService.add(groupId, param);
        return ResponseVO.success();
    }

    @Operation(summary = "修改用户组角色")
    @PatchMapping("/{groupId}/role/{roleId}")
    public ResponseVO<Void> patch(@Validated @NotNull(message = "groupId is null") @PathVariable("groupId") Long groupId,
                                  @Validated @NotNull(message = "roleId is null") @PathVariable("roleId") Long roleId,
                                  @RequestBody GroupRoleUpdateParam param) {
        groupRoleService.patch(groupId, roleId, param);
        return ResponseVO.success();
    }

    @Operation(summary = "删除用户组角色")
    @DeleteMapping("/{groupId}/role/{roleId}")
    public ResponseVO<Void> delete(@Validated @NotNull(message = "groupId is null") @PathVariable("groupId") Long groupId,
                                   @Validated @NotNull(message = "roleId is null") @PathVariable("roleId") Long roleId) {
        groupRoleService.delete(groupId, roleId);
        return ResponseVO.success();
    }


    @Operation(summary = "批量删除用户组角色")
    @DeleteMapping("/{groupId}/roles")
    public ResponseVO<Void> batchDelete(@Validated @NotNull(message = "groupId is null") @PathVariable("groupId") Long groupId,
                                        @RequestBody @Validated RoleBatchDeleteParam param) {
        groupRoleService.batchDelete(groupId, param.getRoleIds());
        return ResponseVO.success();
    }

}
