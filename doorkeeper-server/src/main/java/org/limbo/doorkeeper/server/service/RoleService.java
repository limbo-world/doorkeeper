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

package org.limbo.doorkeeper.server.service;

import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.RoleAddParam;
import org.limbo.doorkeeper.api.model.param.RoleQueryParam;
import org.limbo.doorkeeper.api.model.param.RoleUpdateParam;
import org.limbo.doorkeeper.api.model.vo.RoleVO;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/19 5:36 PM
 */
public interface RoleService {

    RoleVO addRole(Long projectId, RoleAddParam param);

    Integer updateRole(Long projectId, RoleUpdateParam param);

    Integer deleteRole(Long projectId, List<Long> roleIds);

    Page<RoleVO> queryRole(Long projectId, RoleQueryParam param);
}
