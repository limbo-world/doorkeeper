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
import org.limbo.authc.api.interfaces.beans.po.RolePermissionPolicyPO;

import java.util.Collection;
import java.util.List;

/**
 * 角色-权限 关联操作
 *
 * @author Brozen
 * @date 2020/3/4 9:47 AM
 * @email brozen@qq.com
 */
public interface RolePermissionPolicyMapper {

    void addPolicies(@Param("policies") List<RolePermissionPolicyPO> policies);

    void deletePolicies(@Param("projectId") Long projectId,
                        @Param("roleId") Long roleId);

    void deletePoliciesNotIn(@Param("projectId") Long projectId,
                             @Param("roleId") Long roleId,
                             @Param("permCodes") Collection<String> permCodes);

    void deletePoliciesByPermission(@Param("projectId") Long projectId,
                                    @Param("permCode") String permCode);

    List<RolePermissionPolicyPO> getPolicies(@Param("projectId") Long projectId,
                                             @Param("roleIds") Collection<Long> roleIds,
                                             @Param("policy") String policy);

    List<String> getAllowedPermCodes(@Param("projectId") Long projectId,
                                     @Param("roleIds") Collection<Long> roleIds);

    List<String> getRefusedPermCodes(@Param("projectId") Long projectId,
                                     @Param("roleIds") Collection<Long> roleIds);
}
