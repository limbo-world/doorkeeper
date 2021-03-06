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

package org.limbo.doorkeeper.server.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.limbo.doorkeeper.server.dal.entity.Client;

/**
 * @author Devil
 * @date 2021/1/3 6:08 下午
 */
public interface ClientMapper extends BaseMapper<Client> {

    @Select("select * from client where realm_id = #{realmId} and client_id =#{clientId}")
    Client getById(@Param("realmId") Long realmId, @Param("clientId") Long clientId);

    @Select("select * from client where realm_id = #{realmId} and name =#{name}")
    Client getByName(@Param("realmId") Long realmId, @Param("name") String name);

}
