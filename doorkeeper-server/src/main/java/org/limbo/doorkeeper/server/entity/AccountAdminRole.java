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

package org.limbo.doorkeeper.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *
 * 账户在项目下有哪些管理端的角色
 *
 * @Author Devil
 * @Date 2020/12/3 11:08 上午
 */
@Data
@TableName("l_account_admin_role")
public class AccountAdminRole {

    @TableId(type = IdType.AUTO)
    private Long accountAdminRoleId;
    /**
     * 对应的项目ID
     */
    private Long projectId;

    private Long accountId;
    /**
     * 管理端项目的roleId project isAdminProject = true
     */
    private Long roleId;
}
