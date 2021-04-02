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

package org.limbo.doorkeeper.api.model.param.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.vo.GroupUserVO;

/**
 * @author Devil
 * @date 2021/1/9 7:56 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupUserQueryParam extends Page<GroupUserVO> {

    private Long realmId;

    private Long groupId;

    @Schema(description = "角色名称，精确匹配")
    private String name;

    @Schema(description = "角色名称，模糊匹配")
    private String dimName;

    @Schema(description ="是否加入用户组")
    private Boolean isJoin;

}
