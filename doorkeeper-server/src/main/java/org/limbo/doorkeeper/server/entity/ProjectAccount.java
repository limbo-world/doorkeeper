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

import java.util.Date;

/**
 *
 * 项目下绑定了哪些账户，以及对应的属性
 *
 * @Author Devil
 * @Date 2020/12/3 11:06 上午
 */
@Data
@TableName("l_project_account")
public class ProjectAccount {

    @TableId(type = IdType.AUTO)
    private Long projectAccountId;

    private Long projectId;

    private Long accountId;
    /**
     * 是否项目管理员
     * 1. 普通项目 拥有当前项目所有 doorkeeper 接口权限
     * 2. doorkeeper 管理项目
     *      2.1 包含 1 的内容
     *      2.2 可以创建项目
     *      2.3 可以进行项目账户绑定 并且可以指定其他项目的管理员
     */
    private Boolean isAdmin;

    private Date gmtCreated;

    private Date gmtModified;

}
