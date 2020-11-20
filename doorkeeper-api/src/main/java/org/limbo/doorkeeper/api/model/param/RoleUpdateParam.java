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
import javax.validation.constraints.Size;

/**
 * @author Devil
 * @date 2020/11/19 5:42 PM
 */
@Data
public class RoleUpdateParam {

    @NotNull
    private Long projectId;

    @NotNull
    private Long roleId;

    /**
     * 角色名称
     */
    @NotBlank
    @Schema(name = "角色名称")
    private String name;

    /**
     * 角色描述
     */
    @Size(max = 150)
    @Schema(name = "角色描述")
    private String describe;

    /**
     * 默认角色会在用户创建的时候直接绑定
     */
    @NotNull
    @Schema(name = "默认角色会在用户创建的时候直接绑定")
    private Boolean isDefault;

    /**
     * 授予角色的时候是否需要工单审核
     */
    @NotNull
    @Schema(name = "授予角色的时候是否需要工单审核")
    private Boolean needOrder;
}