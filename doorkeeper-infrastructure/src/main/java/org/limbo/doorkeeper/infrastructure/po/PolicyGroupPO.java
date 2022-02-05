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

/**
 * 基于组的策略
 * @author Devil
 * @since 2020/12/31 3:13 下午
 */
@Data
@TableName("doorkeeper_policy_group")
public class PolicyGroupPO {

    @TableId(type = IdType.AUTO)
    private Long policyGroupId;

    private Long policyId;

    private Long groupId;
    /**
     * 是否向下延伸
     * true的情况下，用户在子用户组也满足策略
     */
    private Boolean isExtend;

}
