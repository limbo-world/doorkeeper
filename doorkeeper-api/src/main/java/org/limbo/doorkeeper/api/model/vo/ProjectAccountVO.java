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
 *
 * 账户可以访问哪些项目
 *
 * @Author Devil
 * @Date 2020/12/3 11:06 上午
 */
@Data
public class ProjectAccountVO {

    @Schema(name = "项目账户ID")
    private Long projectAccountId;

    @Schema(name = "账户ID")
    private Long accountId;

    @Schema(name = "项目ID")
    private Long projectId;

    @Schema(name = "项目名称")
    private String projectName;

    @Schema(name = "是否超级管理员")
    private Boolean isSuperAdmin;

    @Schema(name = "是否管理员")
    private Boolean isAdmin;

    @Schema(title = "账户名称")
    private String username;

    @Schema(title = "昵称")
    private String nickname;

    @Schema(title = "账户描述")
    private String accountDescribe;
}
