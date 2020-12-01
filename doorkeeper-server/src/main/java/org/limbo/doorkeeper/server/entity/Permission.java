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
 * @date 2020/11/18 7:12 PM
 */
@Data
@TableName("l_permission")
public class Permission {

    @TableId(type = IdType.AUTO)
    private Long permissionId;

    private Long projectId;
    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 描述
     */
    private String permissionDescribe;
    /**
     * 方法类型 get post
     */
    private String httpMethod;

    /**
     * ant风格url
     */
    private String url;
    /**
     * 是否上线 下线权限不生效
     */
    private Boolean isOnline;

    private Date gmtCreated;

    private Date gmtModified;
}
