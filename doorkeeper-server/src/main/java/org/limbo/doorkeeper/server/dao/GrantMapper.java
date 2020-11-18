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
import org.limbo.authc.api.interfaces.beans.po.AccountPO;
import org.limbo.authc.api.interfaces.beans.po.RolePO;
import org.limbo.authc.api.interfaces.utils.tuple.Tuple;

import java.util.Collection;
import java.util.List;

/**
 * 授权操作
 *
 * @author Brozen
 * @date 2020/3/4 9:44 AM
 * @email brozen@qq.com
 */
public interface GrantMapper {

    Integer grantRoleToAccounts(@Param("projectId") Long projectId,
                                @Param("roleId") Long roleId,
                                @Param("accountIds") Collection<Long> accountIds);

    Integer grantRolesToAccount(@Param("projectId") Long projectId,
                                @Param("roleIds") List<Long> roleIds,
                                @Param("accountId") Long accountId);

    Integer revoke(@Param("projectId") Long projectId,
                   @Param("roleId") Long roleId);

    Integer revokeFromAccountNotIn(@Param("projectId") Long projectId,
                                   @Param("roleId") Long roleId,
                                   @Param("accountIds") Collection<Long> accountIds);

    Integer revokeFromAccountIn(@Param("projectId") Long projectId,
                                @Param("roleId") Long roleId,
                                @Param("accountIds") List<Long> accountIds);

    Integer revokeFromAccounts(@Param("projectId") Long projectId,
                               @Param("accountIds") List<Long> accountIds);

    List<Tuple<Long, String>> getGrantedUserNicks(@Param("projectId") Long projectId,
                                                  @Param("roleIds") Collection<Long> roleIds);

    List<AccountPO> getGrantedUserAccounts(@Param("projectId") Long projectId,
                                           @Param("roleId") Long roleId);

    List<RolePO> getGrantedRoles(@Param("projectId") Long projectId,
                                 @Param("accountIds") Collection<Long> accountIds);

    List<Long> getGrantedRoleIds(@Param("projectId") Long projectId,
                                 @Param("accountIds") Collection<Long> accountIds);
}
