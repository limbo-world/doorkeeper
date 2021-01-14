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

package org.limbo.doorkeeper.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.limbo.doorkeeper.api.model.param.resource.ResourceQueryParam;
import org.limbo.doorkeeper.api.model.vo.ResourceVO;
import org.limbo.doorkeeper.server.entity.Resource;

import java.util.List;

/**
 * @author Devil
 * @date 2021/1/3 6:08 下午
 */
public interface ResourceMapper extends BaseMapper<Resource> {

    @Select("select * from resource where realm_id = #{realId} and" +
            " client_id =#{clientId} and resource_id = #{resourceId}")
    Resource getById(@Param("realId") Long realId, @Param("clientId") Long clientId,
                       @Param("resourceId") Long resourceId);

    @Select("select * from resource where realm_id = #{realId} and" +
            " client_id =#{clientId} and name = #{name}")
    Resource getByName(@Param("realId") Long realId, @Param("clientId") Long clientId,
                     @Param("name") String name);

    long pageVOCount(ResourceQueryParam param);

    List<ResourceVO> pageVOS(ResourceQueryParam param);

}
