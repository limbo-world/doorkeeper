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
import org.limbo.doorkeeper.api.dto.param.query.PermissionQueryParam;
import org.limbo.doorkeeper.api.dto.vo.PermissionVO;
import org.limbo.doorkeeper.server.infrastructure.po.PermissionPO;

import java.util.List;

/**
 * @author Devil
 * @since 2021/1/3 6:08 下午
 */
public interface PermissionMapper extends BaseMapper<PermissionPO> {

    @Select("select * from permission where realm_id = #{realmId} and" +
            " client_id =#{clientId} and permission_id = #{permissionId}")
    PermissionPO getById(@Param("realmId") Long realmId, @Param("clientId") Long clientId,
                         @Param("permissionId") Long permissionId);

    long voCount(PermissionQueryParam param);

    List<PermissionVO> getVOS(PermissionQueryParam param);

    PermissionVO getVO(@Param("realmId") Long realmId, @Param("clientId") Long clientId, @Param("permissionId") Long permissionId);

}
