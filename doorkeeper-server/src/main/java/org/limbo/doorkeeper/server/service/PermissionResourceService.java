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

package org.limbo.doorkeeper.server.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.model.vo.PermissionResourceVO;
import org.limbo.doorkeeper.server.dal.entity.PermissionResource;
import org.limbo.doorkeeper.server.dal.mapper.PermissionResourceMapper;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/8 10:48 上午
 */
@Service
public class PermissionResourceService {

    @Autowired
    private PermissionResourceMapper permissionResourceMapper;

    public List<PermissionResourceVO> getByPermissionId(Long permissionId) {
        List<PermissionResource> permissionResources = permissionResourceMapper.selectList(Wrappers.<PermissionResource>lambdaQuery()
                .eq(PermissionResource::getPermissionId, permissionId)
        );
        return permissionResources == null ? new ArrayList<>() : EnhancedBeanUtils.createAndCopyList(permissionResources, PermissionResourceVO.class);
    }

    @Transactional
    public void update(Long permissionId, List<Long> resourceIds) {
        // 删除
        permissionResourceMapper.delete(Wrappers.<PermissionResource>lambdaQuery()
                .eq(PermissionResource::getPermissionId, permissionId)
        );
        if (CollectionUtils.isEmpty(resourceIds)) {
            return;
        }
        // 新增
        List<PermissionResource> permissionResources = new ArrayList<>();
        for (Long resourceId : resourceIds) {
            PermissionResource permissionResource = new PermissionResource();
            permissionResource.setPermissionId(permissionId);
            permissionResource.setResourceId(resourceId);
            permissionResources.add(permissionResource);
        }
        batchSave(permissionResources);
    }

    @Transactional
    public void batchSave(List<PermissionResource> permissionResources) {
        if (CollectionUtils.isEmpty(permissionResources)) {
            return;
        }
        permissionResourceMapper.batchInsertIgnore(permissionResources);
    }

}
