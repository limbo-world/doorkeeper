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

package org.limbo.doorkeeper.server.infrastructure.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.limbo.doorkeeper.server.infrastructure.po.GroupPO;
import org.limbo.doorkeeper.server.infrastructure.mapper.GroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Devil
 * @since 2021/2/20 4:58 下午
 */
@Repository
public class GroupDao {

    @Autowired
    private GroupMapper groupMapper;

    public List<GroupPO> getDefaultGroup(Long realmId) {
        return groupMapper.selectList(Wrappers.<GroupPO>lambdaQuery()
                .eq(GroupPO::getRealmId, realmId)
                .eq(GroupPO::getIsDefault, true)
        );
    }

}
