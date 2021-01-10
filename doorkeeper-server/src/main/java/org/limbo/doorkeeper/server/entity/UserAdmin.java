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
import org.limbo.doorkeeper.server.constants.UserAdminType;

/**
 * DK 也就是管理端Realm下用户的关系类型
 *
 * @author Devil
 * @date 2021/1/10 10:17 下午
 */
@Data
@TableName("user_admin")
public class UserAdmin {

    @TableId(type = IdType.AUTO)
    private Long userId;

    private Long realmId;
    /**
     * 0 表示为realm的关系类型 不为0则为client的关系属性
     */
    private Long clientId;

    private UserAdminType type;
}
