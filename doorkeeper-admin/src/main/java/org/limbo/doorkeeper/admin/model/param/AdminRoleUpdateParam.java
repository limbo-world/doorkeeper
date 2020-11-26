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

package org.limbo.doorkeeper.admin.model.param;

import lombok.Data;
import org.limbo.doorkeeper.api.model.param.RolePermissionAddParam;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/26 9:43 AM
 */
@Data
public class AdminRoleUpdateParam {

    private Long roleId;

    private String roleName;

    private String roleDescribe;

    private Boolean isDefault;

    private List<RolePermissionAddParam> addRolePermissions;

    private List<Long> deleteRolePermissionIds;
}
