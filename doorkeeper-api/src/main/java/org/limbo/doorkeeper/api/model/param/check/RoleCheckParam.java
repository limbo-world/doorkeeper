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

package org.limbo.doorkeeper.api.model.param.check;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Devil
 * @date 2021/1/29 12:39 下午
 */
@Data
public class RoleCheckParam {

    @Schema(title = "角色ID列表")
    private List<Long> roleIds;

    @Schema(title = "委托方ID", description = "域角色传 0")
    private Long clientId;

    @Schema(title = "名称", description = "精确查询")
    private String name;

    @Schema(title = "名称", description = "模糊查询")
    private String dimName;

    @Schema(title = "角色名称列表", description = "精确查询")
    private List<String> names;

}
