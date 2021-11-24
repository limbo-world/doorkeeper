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

package org.limbo.doorkeeper.api.model.param.query;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import org.limbo.doorkeeper.api.model.vo.RoleVO;

import javax.validation.constraints.NotNull;

/**
 * @author Devil
 * @since 2021/1/4 5:44 下午
 */
@Data
public class RoleQueryParam extends PageParam<RoleVO> {

    private Long realmId;

    @NotNull(message = "命名空间不能为空")
    @Parameter(description = "命名空间 如果是域角色 为 0", required = true)
    private Long namespaceId;

    @Parameter(description = "名称，精确查询")
    private String name;

    @Parameter(description = "名称，模糊查询")
    private String dimName;

    @Parameter(description ="是否启用")
    private Boolean isEnabled;

    @Parameter(description ="默认添加")
    private Boolean isDefault;
}
