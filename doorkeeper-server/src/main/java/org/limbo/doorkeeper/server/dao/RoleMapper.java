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

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.limbo.doorkeeper.api.model.param.RoleQueryParam;
import org.limbo.doorkeeper.server.entity.Role;

import java.util.List;

/**
 * 角色操作
 *
 * @author Brozen
 * @date 2020/2/27 5:04 PM
 * @email brozen@qq.com
 */
public interface RoleMapper extends BaseMapper<Role> {

    @Select("select * from l_role where project_id = #{projectId} and role_id = #{roleId}")
    Role getRole(@Param("projectId") Long projectId,
                 @Param("roleId") Long roleId);

    @Select("select * from l_role where project_id = #{projectId}")
    List<Role> getRoles(@Param("projectId") Long projectId);

    @Select("select * from l_role where role_name = #{roleName} and project_id = #{projectId}")
    Role getByRoleName(@Param("projectId") Long projectId,
                       @Param("roleName") String roleName);

    @Delete("update l_role set is_deleted = 1 where project_id = #{projectId} and role_id = #{roleId}")
    Integer deleteRole(@Param("projectId") Long projectId,
                       @Param("roleId") Long roleId);

    Long countRole(RoleQueryParam param);

    List<Role> queryRole(RoleQueryParam param);
}
