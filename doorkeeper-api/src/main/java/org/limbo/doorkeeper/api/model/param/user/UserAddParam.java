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

package org.limbo.doorkeeper.api.model.param.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Devil
 * @date 2021/1/9 10:57 上午
 */
@Data
public class UserAddParam {

    @NotNull(message = "域不能为空")
    @Schema(title = "域id", required = true)
    private Long realmId;

    @NotNull(message = "用户名不能为空")
    @Schema(title = "用户名", required = true)
    private String username;

    @NotNull(message = "昵称不能为空")
    @Schema(title = "昵称", required = true)
    private String nickname;

    @NotNull(message = "密码不能为空")
    @Schema(title = "密码", required = true)
    private String password;

    @Schema(title = "是否启用")
    private Boolean isEnabled;

}
