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

package org.limbo.doorkeeper.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Devil
 * @date 2020/11/19 4:06 PM
 */
@Data
public class AccountRoleVO {

    @Schema(title = "角色权限id")
    private Long rolePermissionId;

    @Schema(title = "项目ID")
    private Long projectId;

    @Schema(title = "角色ID")
    private Long roleId;

    @Schema(title = "权限ID")
    private Long permissionId;

}
