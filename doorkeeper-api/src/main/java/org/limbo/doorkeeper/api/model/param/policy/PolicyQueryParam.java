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
import lombok.EqualsAndHashCode;
import org.limbo.doorkeeper.api.constants.PolicyType;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;

/**
 * @author Devil
 * @date 2021/1/6 7:53 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PolicyQueryParam extends Page<PolicyVO> {

    @Schema(title = "名称", description = "精确查询")
    private String name;

    @Schema(title = "名称", description = "模糊查询")
    private String dimName;

    @Schema(title = "是否启用")
    private Boolean isEnabled;

    @Schema(title = "类型")
    private PolicyType type;

}
