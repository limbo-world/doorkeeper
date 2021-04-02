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

package org.limbo.doorkeeper.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/4 11:40 上午
 */
@Data
public class PermissionVO {

    private Long permissionId;

    private Long realmId;

    private Long clientId;

    @Schema(description ="名称")
    private String name;

    @Schema(description ="描述")
    private String description;

    @Schema(description ="判断逻辑")
    private String logic;

    @Schema(description ="执行逻辑")
    private String intention;

    @Schema(description ="是否启用")
    private Boolean isEnabled;

    private Date createTime;

    private Date updateTime;

    @Schema(description ="资源列表")
    private List<PermissionResourceVO> resources;

    @Schema(description ="策略列表")
    private List<PermissionPolicyVO>  policies;

}
