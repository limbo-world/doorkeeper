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
 * 菜单API
 *
 * @author Brozen
 * @date 2020/3/6 5:41 PM
 * @email brozen@qq.com
 */
public interface MenuApi {

    /**
     * 新增菜单，可指定关联的权限
     */
    Response<MenuVO> add(MenuVO.AddParam param);

    /**
     * 更新菜单，可更新名称、图标、关联的权限
     * 菜单父子关系、菜单code不可更改
     */
    Response<MenuVO> update(MenuVO.UpdateParam param);

    /**
     * 更新菜单的排序
     */
    Response<Boolean> updateSort(MenuVO.UpdateSortParam param);

    /**
     * 根据菜单code删除菜单，将会删除菜单与全新、菜单与角色之间的关联
     */
    Response<Boolean> delete(MenuVO.DeleteParam param);

    /**
     * 查询全部菜单信息，返回结果中不包含关联的权限信息
     */
    Response<List<MenuVO>> list(Param param);

    /**
     * 分页查询菜单信息，返回结果中不包含关联的权限信息
     * keyword将like匹配menuCode、menuName
     */
    Response<Page<MenuVO>> query(Page<MenuVO> param);

    /**
     * 查询菜单信息，返回关联的权限数据
     */
    Response<MenuVO> get(MenuVO.GetParam param);
}
