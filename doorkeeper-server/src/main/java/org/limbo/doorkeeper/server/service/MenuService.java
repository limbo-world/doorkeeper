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
import org.limbo.authc.api.interfaces.beans.vo.MenuVO;

import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/4 10:05 AM
 * @email brozen@qq.com
 */
public interface MenuService {

    String addMenu(MenuVO.AddParam param);

    Integer updateMenu(MenuVO.UpdateParam param);

    Integer updateSort(MenuVO.UpdateSortParam param);

    Integer deleteMenu(Long projectId, String menuCode);

    List<MenuVO> listMenu(Long projectId);

    MenuVO getMenu(Long projectId, String menuCode);

    Page<MenuVO> queryMenu(Page<MenuVO> param);
}
