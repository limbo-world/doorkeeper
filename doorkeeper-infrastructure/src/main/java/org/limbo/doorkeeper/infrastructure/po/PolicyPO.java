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
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.api.constants.PolicyType;

import java.util.Date;

/**
 * @author Devil
 * @since 2020/12/31 11:00 上午
 */
@Data
@TableName("doorkeeper_policy")
public class PolicyPO {

    @TableId(type = IdType.AUTO)
    private Long policyId;

    private Long realmId;

    private Long clientId;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 策略类型
     */
    private PolicyType type;
    /**
     * 特定的才有 如组合 角色
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
