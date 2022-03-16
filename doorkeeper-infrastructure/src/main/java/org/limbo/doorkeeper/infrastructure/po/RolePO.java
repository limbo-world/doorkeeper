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
 * realmId clientId name 唯一
 *
 * 分为 realm-role 和 namespace-role 是因为可能存在以下情况
 *
 * 公司内部系统，使用一个realm 那对于每个子应用如OA、财务等都是各自的namespace
 *
 * 对于一个对外应用，可能存在多个模块，那每个子模块就是一个namespace
 *
 * @author Devil
 * @since 2020/12/29 4:28 下午
 */
@Data
@TableName("doorkeeper_role")
public class RolePO {

    @TableId(type = IdType.AUTO)
    private Long roleId;
    /**
     * 属于哪个realm
     */
    private Long realmId;
    /**
     * 属于哪个命名空间 如果属于域 则为0
     */
    private Long namespaceId;
    /**
     * 名称 service唯一
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 是否组合
     */
    private Boolean isCombine;
    /**
     * 是否启用
     */
    private Boolean isEnabled;
    /**
     * 是否默认添加
     */
    private Boolean isDefault;

    private Date createTime;

    private Date updateTime;

}
