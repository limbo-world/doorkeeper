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

package org.limbo.doorkeeper.api.dto.param;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Devil
 * @since 2020/12/31 5:29 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginParam {

    @Parameter(description = "域，为空则默认登录doorkeeper域")
    private Long realmId;

    @NotBlank(message = "用户名不能为空")
    @Parameter(description ="用户名", required = true)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Parameter(description ="密码", required = true)
    private String password;

    @NotNull(message = "时间戳不能为空")
    @Parameter(description ="时间戳", required = true)
    private Long timestamp;

}
