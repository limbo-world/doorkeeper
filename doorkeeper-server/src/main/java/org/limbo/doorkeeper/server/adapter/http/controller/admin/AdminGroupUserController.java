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
import org.limbo.doorkeeper.api.dto.param.batch.GroupUserBatchUpdateParam;
import org.limbo.doorkeeper.api.dto.vo.GroupUserVO;
import org.limbo.doorkeeper.api.dto.vo.ResponseVO;
import org.limbo.doorkeeper.server.adapter.http.controller.BaseController;
import org.limbo.doorkeeper.server.service.GroupUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Devil
 * @since 2021/1/5 4:46 下午
 */
@Tag(name = "用户组用户")
@Slf4j
@RestController
@RequestMapping("/api/doorkeeper/v1/admin/realm/{realmId}/group")
public class AdminGroupUserController extends BaseController {

    @Autowired
    private GroupUserService groupUserService;

    @Operation(summary = "查询用户组用户列表")
    @GetMapping("/{groupId}/user")
    public ResponseVO<List<GroupUserVO>> list(@Validated @NotNull(message = "未提交用户组ID") @PathVariable("groupId") Long groupId) {
        return ResponseVO.success(groupUserService.list(groupId));
    }

    // todo
    @Operation(summary = "批量修改用户组用户")
    @PostMapping("/{groupId}/user/batch")
    public ResponseVO<Void> batchUser(@Validated @NotNull(message = "未提交用户组ID") @PathVariable("groupId") Long groupId,
                                      @RequestBody @Validated GroupUserBatchUpdateParam param) {
        groupUserService.batchUpdate(groupId, param);
        return ResponseVO.success();
    }

}
