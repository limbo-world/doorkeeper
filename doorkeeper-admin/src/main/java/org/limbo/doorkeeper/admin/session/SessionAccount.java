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

package org.limbo.doorkeeper.admin.session;

import lombok.Data;

/**
 * @author Devil
 * @date 2020/11/23 8:17 PM
 */
@Data
public class SessionAccount {
    // 账户唯一ID
    private Long accountId;

    // 账户昵称
    private String accountNick;

    // 账户所属项目，admin项目的projectId
    private Long accountProjectId;

    // 当前用户所选的项目
    private Long currentProjectId;

    private String currentProjectName;

    // 是否为超级管理员
    private Boolean isSuperAdmin;
}
