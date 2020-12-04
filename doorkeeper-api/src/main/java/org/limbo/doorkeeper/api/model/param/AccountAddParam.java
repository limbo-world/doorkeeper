/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.limbo.doorkeeper.api.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Devil
 * @date 2020/11/19 3:09 PM
 */
@Data
public class AccountAddParam {

    @NotBlank(message = "用户名不能为空")
    @Schema(title = "账户名称", required = true)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(title = "密码", required = true)
    private String password;

    @NotBlank(message = "昵称不能为空")
    @Schema(title = "昵称", required = true)
    private String nickname;

    @Schema(title = "描述")
    private String accountDescribe;

    @Schema(title = "是否管理员")
    private Boolean isAdmin;

}
