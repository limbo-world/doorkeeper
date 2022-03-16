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
 * 命名空间
 *
 * 大多数情况下，namespace是希望使用 doorkeeper 来保护自己并提供单点登录解决方案的应用程序和服务。
 *
 * @author Devil
 * @since 2020/12/29 4:22 下午
 */
@Data
@TableName("doorkeeper_namespace")
public class NamespacePO {

    @TableId(type = IdType.AUTO)
    private Long namespaceId;
    /**
     * 属于哪个租户
     */
    private Long realmId;
    /**
     * 名称 组户下唯一
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 是否启用
     */
    private Boolean isEnabled;

    private Date createTime;

    private Date updateTime;

}
