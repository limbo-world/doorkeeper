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

package org.limbo.doorkeeper.api.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Devil
 * @date 2020/11/19 7:29 PM
 */
@Data
public class ApiAddParam {

    @NotBlank(message = "api名称不能为空")
    @Schema(title = "api名称", required = true)
    private String apiName;

    @Schema(title = "api描述")
    private String apiDescribe;

    @NotBlank(message = "类型不能为空")
    @Schema(title = "类型 get post", required = true)
    private String apiMethod;

    @NotBlank(message = "url不能为空")
    @Schema(title = "ant风格url", required = true)
    private String apiUrl;
}
