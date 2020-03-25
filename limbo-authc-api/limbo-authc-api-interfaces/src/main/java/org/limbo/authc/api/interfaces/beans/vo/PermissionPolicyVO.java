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

package org.limbo.authc.api.interfaces.beans.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.limbo.authc.api.interfaces.constants.PermissionAuthcPolicies;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *  权限策略，可针对单独的权限设置 允许 或 拒绝 策略
 *
 * @author Brozen
 * @date 2020/3/10 4:17 PM
 * @email brozen@qq.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionPolicyVO implements Serializable {

    private static final long serialVersionUID = 2014298719473842538L;

    @NotNull(message = "权限不存在")
    private String permCode;

    @NotNull(message = "未指定策略")
    private PermissionAuthcPolicies policy;

}
