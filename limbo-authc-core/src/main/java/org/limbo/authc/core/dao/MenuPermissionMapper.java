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

package org.limbo.authc.core.dao;

import org.apache.ibatis.annotations.Param;
import org.limbo.authc.api.interfaces.beans.po.PermissionPO;

import java.util.Collection;
import java.util.List;

/**
 * 菜单-权限 关联操作
 *
 * @author Brozen
 * @date 2020/3/4 9:50 AM
 * @email brozen@qq.com
 */
public interface MenuPermissionMapper {

    void addMenuPermission(@Param("projectId") Long projectId,
                           @Param("menuCode") String menuCode,
                           @Param("permCodes") Collection<String> permCodes);

    void deleteMenuPermission(@Param("projectId") Long projectId,
                              @Param("menuCode") String menuCode);

    void deleteMenuPermissionNotIn(@Param("projectId") Long projectId,
                                   @Param("menuCode") String menuCode,
                                   @Param("permCodes") Collection<String> permCodes);

    Integer deletePermissionRelatedMenu(@Param("projectId") Long projectId,
                                        @Param("permCode") String permCode);

    List<PermissionPO> getMenuPermissions(@Param("projectId") Long projectId,
                                          @Param("menuCodes") Collection<String> menuCodes);

    List<String> getMenuPermissionCodes(@Param("projectId") Long projectId,
                                        @Param("menuCodes") Collection<String> menuCodes);

}
