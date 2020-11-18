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

package org.limbo.doorkeeper.server.dao;

import org.apache.ibatis.annotations.Param;
import org.limbo.authc.api.interfaces.beans.po.MenuPO;
import org.limbo.authc.api.interfaces.utils.tuple.Tuple;

import java.util.Collection;
import java.util.List;

/**
 * 角色-菜单 关联操作
 *
 * @author Brozen
 * @date 2020/3/4 9:46 AM
 * @email brozen@qq.com
 */
public interface RoleMenuMapper {

    void addRoleMenus(@Param("projectId") Long projectId,
                      @Param("roleId") Long roleId,
                      @Param("menuCodes") Collection<String> menuCodes);

    void deleteRoleMenus(@Param("projectId") Long projectId,
                         @Param("roleId") Long roleId);

    void deleteRoleMenusNotIn(@Param("projectId") Long projectId,
                              @Param("roleId") Long roleId,
                              @Param("menuCodes") Collection<String> menuCodes);

    void deleteMenuRelatedRole(@Param("projectId") Long projectId,
                               @Param("menuCode") String menuCode);

    List<MenuPO> getRoleMenus(@Param("projectId") Long projectId,
                              @Param("roleIds") Collection<Long> roleIds);

    List<String> getRoleMenuCodes(@Param("projectId") Long projectId,
                                  @Param("roleIds") Collection<Long> roleIds);

    List<Tuple<Long, String>> getGroupedRoleMenuCodes(@Param("projectId") Long projectId,
                                                      @Param("roleIds") Collection<Long> roleIds);
}
