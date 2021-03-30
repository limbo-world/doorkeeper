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

package org.limbo.doorkeeper.server.dal.dao;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.limbo.doorkeeper.api.model.vo.ResourceVO;
import org.limbo.doorkeeper.api.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.server.dal.mapper.ResourceMapper;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Devil
 * @date 2021/1/24 10:22 下午
 */
@Repository
public class ResourceDao {

    @Autowired
    private ResourceMapper resourceMapper;

    public List<ResourceVO> getVOSByNames(Long realmId, Long clientId,
                                          List<String> names, Boolean isEnabled) {
        Verifies.notNull(realmId, "域不能为空");
        Verifies.notNull(clientId, "委托方不能为空");
        Verifies.notNull(names, "名称列表不能为空");
        return resourceMapper.getVOS(realmId, clientId, null, names, null, isEnabled);
    }

    public List<ResourceVO> getAllVOS(Long realmId, Long clientId, Boolean isEnabled) {
        Verifies.notNull(realmId, "域不能为空");
        Verifies.notNull(clientId, "委托方不能为空");
        return resourceMapper.getVOS(realmId, clientId, null, null, null, isEnabled);
    }

    public List<ResourceVO> getVOSByResourceIds(Long realmId, Long clientId,
                                                List<Long> resourceIds, Boolean isEnabled) {
        Verifies.notNull(realmId, "域不能为空");
        Verifies.notNull(clientId, "委托方不能为空");
        Verifies.notNull(resourceIds, "资源ID列表不能为空");
        return resourceMapper.getVOS(realmId, clientId, resourceIds, null, null, isEnabled);
    }

    public List<ResourceVO> getVOSByTagCombos(Long realmId, Long clientId,
                                              List<Map<String, String>> tagCombos, Boolean isEnabled) {
        Verifies.notNull(realmId, "域不能为空");
        Verifies.notNull(clientId, "委托方不能为空");
        Verifies.notNull(tagCombos, "标签组合列表不能为空");
        List<ResourceVO> result = new ArrayList<>();
        for (Map<String, String> tagMap : tagCombos) {
            if (MapUtils.isEmpty(tagMap)) {
                continue;
            }
            List<String> kvs = new ArrayList<>();
            for (Map.Entry<String, String> entry : tagMap.entrySet()) {
                kvs.add(entry.getKey() + DoorkeeperConstants.KV_DELIMITER + entry.getValue());
            }
            List<ResourceVO> vosByTags = resourceMapper.getVOS(realmId, clientId, null, null, kvs, isEnabled);
            if (CollectionUtils.isNotEmpty(vosByTags)) {
                result.addAll(vosByTags);
            }
        }
        return result;
    }

}
