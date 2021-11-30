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

package org.limbo.doorkeeper.api.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Devil
 * @since 2021/1/9 10:54 上午
 */
@Data
public class UserVO {

    private Long userId;

    private Long realmId;

    @Schema(description ="用户名")
    private String username;

    @Schema(description ="昵称")
    private String nickname;

    @Schema(description ="描述")
    private String description;

    @Schema(description ="邮箱")
    private String email;

    @Schema(description ="电话")
    private String phone;

    @Schema(description ="扩展信息")
    private String extend;

    @Schema(description ="是否启用")
    private Boolean isEnabled;
}
