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

package org.limbo.doorkeeper.server.adapter.http.controller.common;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.dto.param.update.PasswordUpdateParam;
import org.limbo.doorkeeper.api.dto.vo.AccountGrantVO;
import org.limbo.doorkeeper.api.dto.vo.ResponseVO;
import org.limbo.doorkeeper.api.dto.vo.UserVO;
import org.limbo.doorkeeper.server.adapter.http.controller.BaseController;
import org.limbo.doorkeeper.application.service.LoginService;
import org.limbo.doorkeeper.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Devil
 * @since 2021/1/4 10:45 上午
 */
@Tag(name = "会话")
@Slf4j
@RestController
@RequestMapping("/api/doorkeeper/v1/doorkeeper/v1/session")
public class SessionController extends BaseController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @Operation(summary = "用于校验会话是否过期")
    @GetMapping("/check")
    public ResponseVO<Void> check() {
        return ResponseVO.success();
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/user-info")
    public ResponseVO<UserVO> userInfo() {
        return ResponseVO.success(getUser());
    }

    @Operation(summary = "刷新token过期时间")
    @GetMapping("/refresh")
    public ResponseVO<String> refresh() {
        return ResponseVO.success(loginService.refreshToken(getToken()));
    }

    @Operation(summary = "修改密码")
    @PutMapping("/change-password")
    public ResponseVO<String> changePassword(@RequestBody PasswordUpdateParam param) {
        UserVO user = getUser();
        userService.changePassword(user.getRealmId(), user.getUserId(), param);
        return ResponseVO.success();
    }

    // todo
    @Operation(summary = "获取当前项目页面权限信息")
    @GetMapping("/grant-info")
    public ResponseVO<AccountGrantVO> getGrantInfo() {
        // 拿到用户管理端权限
        return ResponseVO.success();
    }

}
