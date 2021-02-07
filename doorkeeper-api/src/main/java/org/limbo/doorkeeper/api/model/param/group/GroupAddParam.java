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

package org.limbo.doorkeeper.api.model.param.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.limbo.doorkeeper.api.model.param.policy.PolicyGroupAddParam;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/9 10:57 上午
 */
@Data
public class GroupAddParam {

    @NotNull(message = "名称不能为空")
    @Schema(title = "名称", required = true)
    private String name;

    @Schema(title = "描述")
    private String description;

    @Schema(title = "父ID")
    private Long parentId;

    @Schema(title = "默认添加")
    private Boolean isDefault;

    @Schema(title = "添加的用户", description = "将哪些用户加入用户组")
    private List<Long> userIds;

    @Schema(title = "添加的策略", description = "将用户组加入哪些策略")
    private List<PolicyGroupAddParam> policies;

    @Schema(title = "添加的角色", description = "将哪些角色加入用户组")
    private List<GroupRoleAddParam> roles;
}
