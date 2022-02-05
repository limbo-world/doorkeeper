/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   	http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.limbo.doorkeeper.infrastructure.dao.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.limbo.doorkeeper.server.infrastructure.po.UserPO;

/**
 * @author Devil
 * @since 2020/12/31 5:44 下午
 */
public interface UserMapper extends BaseMapper<UserPO> {

    @Select("select * from doorkeeper_user where realm_id = #{realmId} and username = #{username}")
    UserPO getByUsername(@Param("realmId") Long realmId, @Param("username") String username);

    @Select("select * from doorkeeper_user where realm_id = #{realmId} and user_id = #{userId}")
    UserPO getById(@Param("realmId") Long realmId, @Param("userId") Long userId);
}
