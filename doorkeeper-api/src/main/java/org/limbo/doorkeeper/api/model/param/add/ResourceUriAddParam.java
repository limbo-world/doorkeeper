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
import org.limbo.doorkeeper.api.constants.UriMethod;

import javax.validation.constraints.NotBlank;

/**
 * @author Devil
 * @since 2021/1/5 4:52 下午
 */
@Data
public class ResourceUriAddParam {

    @NotBlank(message = "uri不能为空")
    @Schema(required = true, description = "uri，ant 风格")
    private String uri;

    @Schema(description = "http method 如果为空 表示适配所有请求方式")
    private UriMethod method;
}
