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

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Devil
 * @since 2021/1/6 4:58 下午
 */
@Data
public class PolicyVO {

    private Long policyId;

    private Long realmId;

    private Long clientId;

    @Schema(description ="名称")
    private String name;

    @Schema(description ="描述")
    private String description;

    @Schema(description ="类型")
    private String type;

    @Schema(description = "判断逻辑，组合策略、角色策略需要")
    private String logic;

    @Schema(description ="执行逻辑")
    private String intention;

    @Schema(description ="是否启用")
    private Boolean isEnabled;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Schema(description ="角色策略")
    private List<PolicyRoleVO>  roles;

    @Schema(description ="用户策略")
    private List<PolicyUserVO>  users;

    @Schema(description ="用户组策略")
    private List<PolicyGroupVO> groups;
}
