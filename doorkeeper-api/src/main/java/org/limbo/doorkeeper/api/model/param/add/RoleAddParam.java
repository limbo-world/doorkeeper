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

package org.limbo.doorkeeper.api.model.param.add;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/4 2:57 下午
 */
@Data
public class RoleAddParam {

    @NotNull(message = "委托方不能为空")
    @Schema(description = "委托方，如果是域角色 clientId 为 0", required = true)
    private Long clientId;

    @NotBlank(message = "名称不能为空")
    @Schema(description ="名称", required = true)
    private String name;

    @Schema(description ="描述")
    private String description;

    @Schema(description ="是否启用")
    private Boolean isEnabled;

    @Schema(description ="是否默认添加")
    private Boolean isDefault;

    @Schema(description = "添加的用户组，新增的时候同时将角色加入此用户组")
    private List<RoleGroupAddParam> groups;

    @Schema(description = "用户列表，新增的时候同时将用户加入此角色")
    private List<Long> userIds;

    @Schema(description = "策略列表，新增的时候同时将角色加入此策略")
    private List<Long> policyIds;
}
