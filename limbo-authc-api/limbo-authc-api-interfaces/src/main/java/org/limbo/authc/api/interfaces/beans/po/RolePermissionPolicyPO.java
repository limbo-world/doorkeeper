/*
 *  Copyright 2020-2024 Limbo Team (https://github.com/LimboHome).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.limbo.authc.api.interfaces.beans.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Brozen
 * @date 2020/3/10 3:53 PM
 * @email brozen@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("l_role_permission_policy")
public class RolePermissionPolicyPO extends BasePO {

    private static final long serialVersionUID = 1508281982149300586L;

    private Long roleId;

    private String permCode;

    private String policy;

}
