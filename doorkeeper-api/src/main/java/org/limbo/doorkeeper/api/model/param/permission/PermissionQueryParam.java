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

package org.limbo.doorkeeper.api.model.param.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;

/**
 * @author Devil
 * @date 2021/1/8 9:32 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionQueryParam extends Page<PermissionVO> {

    @Schema(title = "名称", description = "精确查询")
    private String name;

    @Schema(title = "名称", description = "模糊查询")
    private String dimName;

    @Schema(title = "判断逻辑")
    private Logic logic;

    @Schema(title = "执行逻辑")
    private Intention intention;

    @Schema(title = "是否启用")
    private Boolean isEnabled;

}
