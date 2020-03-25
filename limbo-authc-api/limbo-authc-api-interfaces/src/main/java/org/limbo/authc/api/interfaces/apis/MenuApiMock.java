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

import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.Param;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.MenuVO;

import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/13 2:53 PM
 * @email brozen@qq.com
 */
public class MenuApiMock implements MenuApi {
    @Override
    public Response<MenuVO> add(MenuVO.AddParam param) {
        return Response.serviceError("服务暂不可用");
    }

    @Override
    public Response<MenuVO> update(MenuVO.UpdateParam param) {
        return Response.serviceError("服务暂不可用");
    }

    @Override
    public Response<Boolean> updateSort(MenuVO.UpdateSortParam param) {
        return Response.serviceError("服务暂不可用");
    }

    @Override
    public Response<Boolean> delete(MenuVO.DeleteParam param) {
        return Response.serviceError("服务暂不可用");
    }

    @Override
    public Response<List<MenuVO>> list(Param param) {
        return Response.serviceError("服务暂不可用");
    }

    @Override
    public Response<Page<MenuVO>> query(Page<MenuVO> param) {
        return Response.serviceError("服务暂不可用");
    }

    @Override
    public Response<MenuVO> get(MenuVO.GetParam param) {
        return Response.serviceError("服务暂不可用");
    }
}
