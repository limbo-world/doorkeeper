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

package org.limbo.doorkeeper.api.model.param.policy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.api.constants.PolicyType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/5 4:48 下午
 */
@Data
public class PolicyAddParam {

    @NotBlank(message = "名称不能为空")
    @Schema(title = "名称", required = true)
    private String name;

    @Schema(title = "描述")
    private String description;

    @NotNull(message = "类型不能为空")
    @Schema(title = "类型", required = true)
    private PolicyType type;

    @Schema(title = "判断逻辑", description = "只有组合策略需要")
    private Logic logic;

    @NotNull(message = "执行逻辑不能为空")
    @Schema(title = "执行逻辑", required = true)
    private Intention intention;

    @Schema(title = "是否启用")
    private Boolean isEnabled;

    @Schema(title = "参数策略")
    private List<PolicyParamAddParam>  params;

    @Schema(title = "角色策略")
    private List<PolicyRoleAddParam>  roles;

    @Schema(title = "用户策略")
    private List<PolicyUserAddParam>  users;

}
