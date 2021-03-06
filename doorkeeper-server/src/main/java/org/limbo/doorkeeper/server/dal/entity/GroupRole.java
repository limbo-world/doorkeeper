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

package org.limbo.doorkeeper.server.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 用户组角色绑定关系
 * @author Devil
 * @date 2021/1/4 7:11 下午
 */
@Data
public class GroupRole {

    @TableId(type = IdType.AUTO)
    private Long groupRoleId;

    private Long groupId;

    private Long roleId;
    /**
     * 是否向下延伸
     * true的情况下，会把角色传递给子用户组的用户
     */
    private Boolean isExtend;

}
