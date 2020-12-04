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
import javax.validation.constraints.NotNull;

/**
 * @author Devil
 * @date 2020/11/19 3:27 PM
 */
@Data
public class ProjectAccountUpdateParam {

    @NotNull(message = "项目账户ID不能为空")
    @Schema(title = "项目账户ID", required = true)
    private Long projectAccountId;

    @NotBlank(message = "昵称不能为空")
    @Schema(title = "昵称", required = true)
    private String nickname;

    @Schema(title = "描述")
    private String accountDescribe;

    @Schema(title = "是否管理员")
    private Boolean isAdmin;

}
