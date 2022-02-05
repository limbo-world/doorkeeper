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

package org.limbo.doorkeeper.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author Devil
 * @since 2021/1/14 7:31 下午
 */
@Data
@TableName("doorkeeper_group")
public class GroupPO {

    @TableId(type = IdType.AUTO)
    private Long groupId;

    private Long realmId;

    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 父级组id 如果为第一级则为0
     */
    private Long parentId;
    /**
     * 是否默认添加
     */
    private Boolean isDefault;

    private Date createTime;

    private Date updateTime;

}
