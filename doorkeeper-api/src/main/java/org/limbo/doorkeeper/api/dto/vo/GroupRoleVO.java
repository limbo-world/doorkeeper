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

package org.limbo.doorkeeper.api.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Devil
 * @since 2021/1/5 1:55 下午
 */
@Data
public class GroupRoleVO {

    @Schema(description ="组角色ID")
    private Long groupRoleId;

    @Schema(description ="组ID")
    private Long groupId;

    @Schema(description ="角色ID")
    private Long roleId;

    @Schema(description = "是否向下延伸，true的情况下，子用户组会继承其角色")
    private Boolean isExtend;

}
