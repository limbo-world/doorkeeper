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
 * @author liuqingtong
 * @date 2020/11/25 18:04
 */
@Data
public class ApiCheckParam {

    @NotNull(message = "访问账户不存在")
    @Schema(name = "访问账户ID", required = true)
    private Long accountId;

    @NotBlank(message = "请求方法不可为空")
    @Schema(name = "Http请求方式", required = true)
    private String method;

    @NotBlank(message = "请求路径不可为空")
    @Schema(name = "Http请求路径", required = true)
    private String path;

}
