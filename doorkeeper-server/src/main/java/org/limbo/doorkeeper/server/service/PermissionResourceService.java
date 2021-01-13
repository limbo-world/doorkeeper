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
import org.limbo.doorkeeper.api.model.param.permission.PermissionResourceAddParam;
import org.limbo.doorkeeper.api.model.vo.PermissionResourceVO;
import org.limbo.doorkeeper.server.dao.PermissionMapper;
import org.limbo.doorkeeper.server.dao.PermissionResourceMapper;
import org.limbo.doorkeeper.server.dao.ResourceMapper;
import org.limbo.doorkeeper.server.entity.Permission;
import org.limbo.doorkeeper.server.entity.PermissionResource;
import org.limbo.doorkeeper.server.entity.Resource;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @date 2021/1/8 10:48 上午
 */
@Service
public class PermissionResourceService {

    @Autowired
    private PermissionResourceMapper permissionResourceMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    public List<PermissionResourceVO> getByPermissionId(Long permissionId) {
        List<PermissionResource> permissionResources = permissionResourceMapper.selectList(Wrappers.<PermissionResource>lambdaQuery()
                .eq(PermissionResource::getPermissionId, permissionId)
        );
        return permissionResources == null ? new ArrayList<>() : EnhancedBeanUtils.createAndCopyList(permissionResources, PermissionResourceVO.class);
    }

    @Transactional
    public void update(Long permissionId, List<PermissionResourceAddParam> params) {
        // 删除
        permissionResourceMapper.delete(Wrappers.<PermissionResource>lambdaQuery()
                .eq(PermissionResource::getPermissionId, permissionId)
        );
        // 新增
        List<PermissionResourceAddParam> addParams = params.stream()
                .filter(obj -> obj.getPermissionResourceId() == null)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(addParams)) {
            return;
        }
        List<PermissionResource> permissionResources = verifyResourceList(permissionId, params);
        Verifies.verify(CollectionUtils.isNotEmpty(permissionResources), "资源列表为空");
        MyBatisPlusUtils.batchSave(permissionResources, PermissionResource.class);
    }

    @Transactional
    public void batchSave(Long permissionId, List<PermissionResourceAddParam> params) {

        List<PermissionResource> permissionResources = verifyResourceList(permissionId, params);

        Verifies.verify(CollectionUtils.isNotEmpty(permissionResources), "资源列表为空");

        MyBatisPlusUtils.batchSave(permissionResources, PermissionResource.class);
    }

    private List<PermissionResource> verifyResourceList(Long permissionId, List<PermissionResourceAddParam> params) {
        Permission permission = permissionMapper.selectById(permissionId);
        Verifies.notNull(permission, "权限不存在");

        List<PermissionResource> list = new ArrayList<>();

        // 排除不是同个client的资源
        List<Long> resourceIds = params.stream().map(PermissionResourceAddParam::getResourceId).collect(Collectors.toList());
        List<Resource> resources = resourceMapper.selectList(Wrappers.<Resource>lambdaQuery()
                .select(Resource::getResourceId)
                .eq(Resource::getRealmId, permission.getRealmId())
                .eq(Resource::getClientId, permission.getClientId())
                .in(Resource::getResourceId, resourceIds)
        );

        for (Resource resource : resources) {
            PermissionResource po = new PermissionResource();
            po.setPermissionId(permissionId);
            po.setResourceId(resource.getResourceId());
            list.add(po);
        }

        return list;
    }

}
