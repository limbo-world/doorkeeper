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
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Devil
 * @since 2021/4/20 2:06 下午
 */
@Data
@Accessors(chain = true)
public class PolicyCheckerParam {

    @Parameter(description = "进行权限校验时附带的参数 k=v")
    private List<String> params;
}
