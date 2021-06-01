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

package org.limbo.doorkeeper.api.model.param.add;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Devil
 * @date 2021/1/6 5:10 下午
 */
@Data
public class PolicyParamAddParam {

    @Schema(description = "策略参数ID，如果存在，表示是一个已经存在")
    private Long policyParamId;

    @NotBlank(message = "键不能为空")
    @Schema(description ="键", required = true)
    private String k;

    @NotBlank(message = "值不能为空")
    @Schema(description ="值", required = true)
    private String v;
}
