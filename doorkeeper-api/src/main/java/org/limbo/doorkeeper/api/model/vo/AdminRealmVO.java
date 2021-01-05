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

package org.limbo.doorkeeper.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Devil
 * @date 2021/1/4 10:56 上午
 */
@Data
public class AdminRealmVO {

    @Schema(title = "用户域id")
    private Long adminRealmId;

    @Schema(title = "用户id")
    private Long userId;

    @Schema(title = "域id")
    private Long realmId;

    @Schema(title = "域名称")
    private String name;

    @Schema(title = "此用户在此域下是否管理员")
    private Boolean isAdmin;
}