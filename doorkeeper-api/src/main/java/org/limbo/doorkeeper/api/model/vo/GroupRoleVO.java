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

package org.limbo.doorkeeper.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Devil
 * @date 2021/1/5 1:55 下午
 */
@Data
public class GroupRoleVO {

    private Long groupRoleId;

    private Long groupId;

    private Long roleId;

    @Schema(description ="角色所属域")
    private Long realmId;

    @Schema(description ="角色所委托方")
    private Long clientId;

    @Schema(description ="角色名称")
    private String name;

    @Schema(description ="角色描述")
    private String description;

    @Schema(description ="是否组合角色")
    private Boolean isCombine;

    @Schema(description ="是否启用")
    private Boolean isEnabled;

    @Schema(description ="是否默认添加")
    private Boolean isDefault;

    @Schema(description = "是否向下延伸，true的情况下，会把角色传递给子用户组的用户")
    private Boolean isExtend;

}
