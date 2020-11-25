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
import lombok.EqualsAndHashCode;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.vo.ApiVO;

import javax.validation.constraints.NotNull;

/**
 * @author Devil
 * @date 2020/11/24 3:55 PM
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiQueryParam extends Page<ApiVO> {

    @Schema(title = "名称")
    private String apiName;

    @Schema(title = "方法类型 get post")
    private String apiMethod;

    @Schema(title = "ant风格url")
    private String apiUrl;
}
