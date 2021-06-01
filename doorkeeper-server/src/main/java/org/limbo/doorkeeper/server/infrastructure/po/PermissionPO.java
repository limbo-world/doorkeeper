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

package org.limbo.doorkeeper.server.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.api.constants.Intention;

import java.util.Date;

/**
 * 针对某一资源进行处理
 *
 * @author Devil
 * @date 2020/12/31 11:00 上午
 */
@Data
@TableName("permission")
public class PermissionPO {

    @TableId(type = IdType.AUTO)
    private Long permissionId;

    private Long realmId;

    private Long clientId;

    private String name;

    private String description;
    /**
     * 判断逻辑
     */
    private Logic logic;
    /**
     * 执行逻辑
     */
    private Intention intention;
    /**
     * 是否启用
     */
    private Boolean isEnabled;

    private Date createTime;

    private Date updateTime;

}
