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

package org.limbo.doorkeeper.server.controller.realm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.user.*;
import org.limbo.doorkeeper.api.model.vo.UserRoleVO;
import org.limbo.doorkeeper.api.model.vo.UserVO;
import org.limbo.doorkeeper.server.controller.BaseController;
import org.limbo.doorkeeper.server.service.UserRoleService;
import org.limbo.doorkeeper.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/5 4:46 下午
 */
@Tag(name = "用户")
@Slf4j
@RestController
@RequestMapping("/admin/realm/{realmId}/user")
public class AdminUserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Operation(summary = "新建用户")
    @PostMapping
    public Response<UserVO> add(@RequestBody @Validated UserAddParam param) {
        return Response.success(userService.add(getRealmId(), param));
    }

    @Operation(summary = "分页查询用户")
    @GetMapping
    public Response<Page<UserVO>> page(@Validated UserQueryParam param) {
        return Response.success(userService.page(getRealmId(), param));
    }

    @Operation(summary = "查询用户")
    @GetMapping("/{userId}")
    public Response<UserVO> get(@Validated @NotNull(message = "未提交用户ID") @PathVariable("userId") Long userId) {
        return Response.success(userService.get(getRealmId(), userId));
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{userId}")
    public Response<Void> update(@Validated @NotNull(message = "未提交用户ID") @PathVariable("userId") Long userId,
                                   @Validated @RequestBody UserUpdateParam param) {
        userService.update(getRealmId(), userId, param);
        return Response.success();
    }

    @Operation(summary = "查询用户角色列表")
    @GetMapping("/{userId}/user-role")
    public Response<List<UserRoleVO>> list(@Validated @NotNull(message = "未提交用户ID") @PathVariable("userId") Long userId,
                                           @Validated UserRoleQueryParam param) {
        return Response.success(userRoleService.list(getRealmId(), userId, param));
    }

    @Operation(summary = "批量修改用户角色")
    @PostMapping("/{userId}/user-role/batch")
    public Response<Void> batch(@Validated @NotNull(message = "未提交用户ID") @PathVariable("userId") Long userId,
                                               @RequestBody @Validated UserRoleBatchUpdateParam param) {
        userRoleService.batchUpdate(userId, param);
        return Response.success();
    }

}
