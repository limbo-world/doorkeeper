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

package org.limbo.doorkeeper.api.model.vo.policy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/6 4:58 下午
 */
@Data
public class PolicyVO {

    private Long policyId;

    private Long realmId;

    private Long clientId;

    @Schema(title = "名称")
    private String name;

    @Schema(title = "描述")
    private String description;

    @Schema(title = "类型")
    private String type;

    @Schema(title = "判断逻辑", description = "组合策略、角色策略需要")
    private String logic;

    @Schema(title = "执行逻辑")
    private String intention;

    @Schema(title = "是否启用")
    private Boolean isEnabled;

    private Date createTime;

    private Date updateTime;

    @Schema(title = "操作策略")
    private List<PolicyParamVO> params;

    @Schema(title = "角色策略")
    private List<PolicyRoleVO>  roles;

    @Schema(title = "用户策略")
    private List<PolicyUserVO>  users;

    @Schema(title = "用户组策略")
    private List<PolicyGroupVO> groups;
}
