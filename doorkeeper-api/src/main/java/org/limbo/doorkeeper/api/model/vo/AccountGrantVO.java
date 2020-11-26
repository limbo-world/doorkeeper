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

package org.limbo.doorkeeper.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author liuqingtong
 * @date 2020/11/25 20:27
 */
@Data
public class AccountGrantVO {

    @Schema(name = "用户ID")
    private Long accountId;

    @Schema(name = "用户授予的角色")
    private List<RoleVO> roles;

    @Schema(name = "用户授予拥有的权限")
    private List<PermissionVO> permissions;

    @Schema(name = "用户授予权限的API")
    private AccountApiGrantVO apis;

}
