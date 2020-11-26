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
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.limbo.doorkeeper.server.entity.Account;

/**
 * @author Brozen
 * @date 2020/2/27 11:39 AM
 * @email brozen@qq.com
 */
public interface AccountMapper extends BaseMapper<Account> {
    @Select("select * from l_account where project_id = #{projectId} and account_id = #{accountId}")
    Account getProjcetAccountById(@Param("projectId") Long projectId, @Param("accountId") Long accountId);
}
