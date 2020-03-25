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
 * @author Brozen
 * @date 2020/3/13 2:52 PM
 * @email brozen@qq.com
 */
public class AuthorizationApiMock implements AuthorizationApi {
    @Override
    public Response<Boolean> grant(AuthorizationVO.GrantParam param) {
        return Response.serviceError("服务暂不可用");
    }

    @Override
    public Response<Boolean> revoke(AuthorizationVO.GrantParam param) {
        return Response.serviceError("服务暂不可用");
    }

    @Override
    public Response<Boolean> updateGrant(AuthorizationVO.RoleGrantParam param) {
        return Response.serviceError("服务暂不可用");
    }

    @Override
    public Response<Boolean> updateGrant(AuthorizationVO.AccountGrantParam param) {
        return Response.serviceError("服务暂不可用");
    }

    @Override
    public Response<Boolean> hasPermission(AuthorizationVO.PermissionCheckParam param) {
        return Response.serviceError("服务暂不可用");
    }

    @Override
    public Response<List<MenuVO>> getMenus(AuthorizationVO.GetMenusParam param) {
        return Response.serviceError("服务暂不可用");
    }
}
