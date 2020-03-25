/*
 *  Copyright 2020-2024 Limbo Team (https://github.com/LimboHome).
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

package org.limbo.authc.api.interfaces.apis;

import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.AuthorizationVO;
import org.limbo.authc.api.interfaces.beans.vo.MenuVO;

import java.util.List;

/**
 * 授权相关接口
 *
 * @author Brozen
 * @date 2020/3/7 4:28 PM
 * @email brozen@qq.com
 */
public interface AuthorizationApi {

    /**
     * 授权接口，将角色授权给账户
     */
    Response<Boolean> grant(AuthorizationVO.GrantParam param);

    /**
     * 收回授权，取消授予账户的角色
     */
    Response<Boolean> revoke(AuthorizationVO.GrantParam param);

    /**
     * 更新单个角色授权的账户信息
     */
    Response<Boolean> updateGrant(AuthorizationVO.RoleGrantParam param);

    /**
     * 更新单个账户授予的角色信息
     */
    Response<Boolean> updateGrant(AuthorizationVO.AccountGrantParam param);

    /**
     * 权限校验，判断用户是否拥有对应权限
     */
    Response<Boolean> hasPermission(AuthorizationVO.PermissionCheckParam param);

    /**
     * 获取角色已授权的菜单
     */
    Response<List<MenuVO>> getMenus(AuthorizationVO.GetMenusParam param);
}
