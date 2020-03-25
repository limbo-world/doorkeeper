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

package org.limbo.authc.api.dubbo.providers;

import org.apache.dubbo.config.annotation.Service;
import org.limbo.authc.api.interfaces.apis.MenuApi;
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.Param;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.MenuVO;
import org.limbo.authc.core.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 菜单API
 *
 * @author Brozen
 * @date 2020/3/6 5:41 PM
 * @email brozen@qq.com
 */
@Service(version = "${dubbo.service.version}", validation="true")
public class MenuApiDubboProvider extends BaseProvider implements MenuApi {

    @Autowired
    private MenuService menuService;

    @Override
    public Response<MenuVO> add(MenuVO.AddParam param) {
        menuService.addMenu(param);
        return Response.ok(menuService.getMenu(param.getProjectId(), param.getMenuCode()));
    }

    @Override
    public Response<MenuVO> update(MenuVO.UpdateParam param) {
        menuService.updateMenu(param);
        return Response.ok(menuService.getMenu(param.getProjectId(), param.getMenuCode()));
    }

    @Override
    public Response<Boolean> updateSort(MenuVO.UpdateSortParam param) {
        return Response.ok(menuService.updateSort(param) == param.getSorts().size());
    }

    @Override
    public Response<Boolean> delete(MenuVO.DeleteParam param) {
        return Response.ok(menuService.deleteMenu(param.getProjectId(), param.getMenuCode()) > 0);
    }

    @Override
    public Response<List<MenuVO>> list(Param param) {
        return Response.ok(menuService.listMenu(param.getProjectId()));
    }

    @Override
    public Response<Page<MenuVO>> query(Page<MenuVO> param) {
        return Response.ok(menuService.queryMenu(param));
    }

    @Override
    public Response<MenuVO> get(MenuVO.GetParam param) {
        return Response.ok(menuService.getMenu(param.getProjectId(), param.getMenuCode()));
    }
}
