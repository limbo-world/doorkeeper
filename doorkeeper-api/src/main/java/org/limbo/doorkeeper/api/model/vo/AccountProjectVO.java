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

import lombok.Data;

/**
 *
 * 账户可以访问哪些项目
 *
 * @Author Devil
 * @Date 2020/12/3 11:06 上午
 */
@Data
public class AccountProjectVO {

    private Long accountProjectId;

    private Long accountId;

    private Long projectId;

    private String projectName;
    /**
     * 是否超级管理员 项目唯一 主要用于修改别的账号为管理员
     */
    private Boolean isSuperAdmin;
    /**
     * 是否管理员
     */
    private Boolean isAdmin;
}
