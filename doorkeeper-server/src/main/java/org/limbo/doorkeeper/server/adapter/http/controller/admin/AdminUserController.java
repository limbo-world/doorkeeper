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
import org.limbo.doorkeeper.api.dto.param.add.UserAddParam;
import org.limbo.doorkeeper.api.dto.param.query.UserQueryParam;
import org.limbo.doorkeeper.api.dto.param.update.UserUpdateParam;
import org.limbo.doorkeeper.api.dto.vo.UserVO;
import org.limbo.doorkeeper.server.adapter.http.controller.BaseController;
import org.limbo.doorkeeper.application.service.UserService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @author Devil
 * @since 2021/1/5 4:46 下午
 */
@Tag(name = "用户")
@Slf4j
@RestController
@RequestMapping("/api/doorkeeper/v1/admin/realm/{realmId}/user")
public class AdminUserController extends BaseController {

    @Autowired
    private UserService userService;

    @Operation(summary = "新建用户")
    @PostMapping
    public ResponseVO<UserVO> add(@RequestBody @Validated UserAddParam param) {
        return ResponseVO.success(userService.add(getRealmId(), param));
    }

    @Operation(summary = "分页查询用户")
    @GetMapping
    public ResponseVO<PageVO<UserVO>> page(@ParameterObject UserQueryParam param) {
        return ResponseVO.success(userService.page(getRealmId(), param));
    }

    @Operation(summary = "根据id查询用户")
    @GetMapping("/{userId}")
    public ResponseVO<UserVO> getById(@Validated @NotNull(message = "未提交用户ID") @PathVariable("userId") Long userId) {
        return ResponseVO.success(userService.get(getRealmId(), userId, null));
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{userId}")
    public ResponseVO<Void> update(@Validated @NotNull(message = "未提交用户ID") @PathVariable("userId") Long userId,
                                   @Validated @RequestBody UserUpdateParam param) {
        userService.update(getRealmId(), userId, param);
        return ResponseVO.success();
    }

}
