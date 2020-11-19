/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.limbo.doorkeeper.api.model.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.vo.RoleVO;

import javax.validation.constraints.NotNull;

/**
 * @author liuqingtong
 * @date 2020/11/19 19:04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleQueryParam extends Page<RoleVO> {

    @NotNull
    private Long projectId;

}
