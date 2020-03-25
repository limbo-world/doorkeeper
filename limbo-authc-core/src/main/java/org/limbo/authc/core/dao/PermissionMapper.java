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

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.limbo.authc.api.interfaces.beans.po.PermissionPO;

import java.util.Collection;
import java.util.List;

/**
 * @author Brozen
 * @date 2020/2/27 4:53 PM
 * @email brozen@qq.com
 */
public interface PermissionMapper extends BaseMapper<PermissionPO> {

    @Select("select * from l_permission where project_id = #{projectId} and perm_code = #{permCode}")
    PermissionPO getPermission(@Param("projectId") Long projectId,
                               @Param("permCode") String permCode);

    @Select("select * from l_permission where project_id = #{projectId} order by gmt_created desc")
    List<PermissionPO> getPermissions(@Param("projectId") Long projectId);

    @Delete("delete from l_permission where project_id = #{projectId} and perm_code = #{permCode}")
    Integer deletePermission(@Param("projectId") Long projectId,
                             @Param("permCode") String permCode);

    @Select("select count(*) from l_permission where project_id = #{projectId} and perm_code = #{permCode}")
    Integer countPermCode(@Param("projectId") Long projectId,
                          @Param("permCode") String permCode);

}
