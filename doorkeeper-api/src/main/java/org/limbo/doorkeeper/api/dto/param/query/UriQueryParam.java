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

package org.limbo.doorkeeper.api.dto.param.query;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import org.limbo.doorkeeper.api.constants.UriMethod;

/**
 * @author Devil
 * @since 2021/4/19 8:29 下午
 */
@Data
public class UriQueryParam {

    @Parameter(description = "方法类型")
    private UriMethod method;

    @Parameter(description = "uri 精准查询")
    private String uri;

    @Parameter(description = "uri 模糊查询")
    private String dimUri;

}
