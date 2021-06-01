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
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.model.vo.PermissionResourceVO;
import org.limbo.doorkeeper.server.infrastructure.po.PermissionResourcePO;
import org.limbo.doorkeeper.server.infrastructure.mapper.PermissionResourceMapper;
import org.limbo.doorkeeper.server.infrastructure.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/8 10:48 上午
 */
@Repository
public class PermissionResourceDao {

    @Autowired
    private PermissionResourceMapper permissionResourceMapper;

    public List<PermissionResourceVO> getByPermissionId(Long permissionId) {
        List<PermissionResourcePO> permissionResources = permissionResourceMapper.selectList(Wrappers.<PermissionResourcePO>lambdaQuery()
                .eq(PermissionResourcePO::getPermissionId, permissionId)
        );
        return permissionResources == null ? new ArrayList<>() : EnhancedBeanUtils.createAndCopyList(permissionResources, PermissionResourceVO.class);
    }

    @Transactional
    public void update(Long permissionId, List<Long> resourceIds) {
        // 删除
        permissionResourceMapper.delete(Wrappers.<PermissionResourcePO>lambdaQuery()
                .eq(PermissionResourcePO::getPermissionId, permissionId)
        );
        if (CollectionUtils.isEmpty(resourceIds)) {
            return;
        }
        // 新增
        List<PermissionResourcePO> permissionResources = new ArrayList<>();
        for (Long resourceId : resourceIds) {
            PermissionResourcePO permissionResource = new PermissionResourcePO();
            permissionResource.setPermissionId(permissionId);
            permissionResource.setResourceId(resourceId);
            permissionResources.add(permissionResource);
        }
        batchSave(permissionResources);
    }

    @Transactional
    public void batchSave(List<PermissionResourcePO> permissionResources) {
        if (CollectionUtils.isEmpty(permissionResources)) {
            return;
        }
        permissionResourceMapper.batchInsertIgnore(permissionResources);
    }

}
