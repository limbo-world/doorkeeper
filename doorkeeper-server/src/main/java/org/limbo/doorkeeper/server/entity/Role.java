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

package org.limbo.doorkeeper.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author Devil
 * @date 2020/11/18 7:07 PM
 */
@Data
@TableName("l_role")
public class Role {

    @TableId(type = IdType.AUTO)
    private Long roleId;

    private Long projectId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String roleDescribe;

    /**
     * 默认角色会在用户创建的时候直接绑定
     * 如果为 true 角色初始化的时候 即使配置 needOrder 也会忽略
     */
    private Boolean isDefault;

    private Date gmtCreated;

    private Date gmtModified;

}
