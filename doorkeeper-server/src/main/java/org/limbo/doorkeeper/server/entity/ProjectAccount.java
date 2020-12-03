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
     * 是否项目超级管理员 主要用于修改别的账号为管理员
     */
    private Boolean isSuperAdmin;
    /**
     * 是否项目管理员 可以有doorkeeper上此项目的所有接口权限
     */
    private Boolean isAdmin;

    private Date gmtCreated;

    private Date gmtModified;

}
