/*
 *  Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.limbo.doorkeeper.server.service;

import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.vo.PermissionVO;

import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/4 10:01 AM
 * @email brozen@qq.com
 */
public interface PermissionService {

    PermissionVO addPermission(PermissionVO.AddParam param);

    Integer updatePermission(PermissionVO.UpdateParam param);

    Integer deletePermission(Long projectId, String permCode);

    List<PermissionVO> listPermission(Long projectId);

    Page<PermissionVO> queryPermission(PermissionVO.QueryParam param);

    PermissionVO getPermission(Long projectId, String permCode);
}
