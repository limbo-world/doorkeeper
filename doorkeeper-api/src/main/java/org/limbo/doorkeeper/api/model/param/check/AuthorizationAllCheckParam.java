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

package org.limbo.doorkeeper.api.model.param.check;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author Devil
 * @date 2021/1/13 3:31 下午
 */
@Data
@Accessors(chain = true)
public class AuthorizationAllCheckParam implements AuthorizationCheckParam<Void> {

    private Long userId;

    @NotNull
    @Schema(title = "进行权限校验时，资源所属委托方", required = true)
    private Long clientId;

    private List<Void> resourceAssigner;

    @Schema(title = "进行权限校验时附带的参数")
    private Map<String, String> params;

}
