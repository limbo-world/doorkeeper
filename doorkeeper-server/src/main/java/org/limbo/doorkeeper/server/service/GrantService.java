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

import org.limbo.authc.api.interfaces.beans.vo.AuthorizationVO;
import org.limbo.authc.api.interfaces.beans.vo.MenuVO;
import org.limbo.authc.api.interfaces.beans.vo.PermissionVO;

import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/4 10:11 AM
 * @email brozen@qq.com
 */
public interface GrantService {

    /**
     * 角色授权，可将多个角色授权给一个账户，也可将一个角色授权给多个账户，或者将多个角色同时授权给多个账户
     * 授权操作后，账户将拥有更多角色
     */
    Integer grantRole(AuthorizationVO.GrantParam param);

    /**
     * 收回角色授权，删除操作
     */
    Integer revokeRole(AuthorizationVO.GrantParam param);

    /**
     * 更新角色的授权信息。只能针对单个角色更新，更新后角色授予的账户将与入参一致
     */
    Integer updateGrant(AuthorizationVO.RoleGrantParam param);

    /**
     * 更新授权信息。只能针对一个账户进行授权信息更新，更新后账户的授权的角色将与传入参数一致
     */
    Integer updateGrant(AuthorizationVO.AccountGrantParam param);

    List<MenuVO> getAccountMenus(Long projectId, Long accountId);

    List<MenuVO> getAnonMenus(Long projectId);

    List<PermissionVO> getAccountPermissions(Long projectId, Long accountId);

    List<PermissionVO> getAnonPermissions(Long projectId);

}
