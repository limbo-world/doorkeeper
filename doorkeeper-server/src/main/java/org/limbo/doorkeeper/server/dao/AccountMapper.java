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
import org.apache.ibatis.annotations.*;
import org.limbo.doorkeeper.api.model.param.AccountPasswordUpdateParam;
import org.limbo.doorkeeper.server.entity.Account;

import java.util.List;

/**
 * @author Brozen
 * @date 2020/2/27 11:39 AM
 * @email brozen@qq.com
 */
public interface AccountMapper extends BaseMapper<Account> {

    String column = " account_id, project_id, username, nick, last_login, is_super_admin ";

    @Insert("replace into l_account (project_id, username, password, nick )"
            + "values (#{projectId}, #{username}, #{password}, #{nick})")
    @SelectKey(keyProperty = "accountId", before = false,
            statement = "select LAST_INSERT_ID()", resultType = Long.class)
    Integer replace(Account po);

    @Select("select " + column + " from l_account where is_activated = 1 and is_deleted = 0 and project_id = #{projectId} and account_id = #{accountId}")
    Account getAccount(@Param("projectId") Long projectId,
                       @Param("accountId") Long accountId);

    @Select("select " + column + " from l_account where is_activated = 1 and is_deleted = 0 and project_id = #{projectId} and username = #{username}")
    Account getAccountByUsername(@Param("projectId") Long projectId,
                                 @Param("username") String username);

    @Select("select " + column + " from l_account where is_activated = 1 and is_deleted = 0 and project_id = #{projectId} " +
            "and account_id = #{accountId} and username = #{username}")
    Account getAccountByIdAndUsername(@Param("projectId") Long projectId,
                                      @Param("accountId") Long accountId,
                                      @Param("username") String username);

    @Select("select " + column + " from l_account where is_activated = 1 and is_deleted = 0 and project_id = #{projectId}")
    List<Account> getAccounts(@Param("projectId") Long projectId);

    @Select("select * from l_account where is_activated = 1 and is_deleted = 0 and project_id = #{projectId} and username = #{username}")
    Account getAccountAllByUsername(@Param("projectId") Long projectId, @Param("username") String username);

    @Select("select * from l_account where is_activated = 1 and is_deleted = 0 and project_id = #{projectId} and account_id = #{accountId}")
    Account getAccountAllById(@Param("projectId") Long projectId, @Param("accountId") Long accountId);

    @Select("select count(*) from l_account where is_activated = 1 and is_deleted = 0 and project_id = #{projectId} and username = #{username}")
    Integer countByUsername(@Param("projectId") Long projectId, @Param("username") String username);

    /**
     * 更新登录时间、IP
     */
    void updateLoginInfo(Account account);

    /**
     * 更新基础信息，nick
     */
    void updateBaseInfo(Account account);

    /**
     * 更新密码
     */
    void updatePassword(AccountPasswordUpdateParam param);

    /**
     * 假删除
     */
    @Update("update l_account set is_deleted = #{isDeleted} where project_id = #{projectId} and account_id = #{accountId}")
    Integer updateDelete(@Param("projectId") Long projectId, @Param("accountId") Long accountId, @Param("isDeleted") Boolean isDeleted);
}
