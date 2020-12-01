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

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Devil
 * @date 2020/11/20 9:30 AM
 */
@Data
public class PermissionAddParam {

    @NotNull(message = "权限名称不能为空")
    @Schema(title = "权限名称", required = true)
    private String permissionName;

    @Schema(title = "权限描述")
    private String permissionDescribe;

    @Schema(title = "http方法")
    private String httpMethod;

    @Schema(title = "ant风格url")
    private String url;

    @Schema(title = "是否上线 下线权限不生效")
    private Boolean isOnline;

}
