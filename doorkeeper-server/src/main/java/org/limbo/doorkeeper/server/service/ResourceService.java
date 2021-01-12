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
import org.limbo.doorkeeper.api.exception.ParamException;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.resource.*;
import org.limbo.doorkeeper.api.model.vo.ResourceTagVO;
import org.limbo.doorkeeper.api.model.vo.ResourceUriVO;
import org.limbo.doorkeeper.api.model.vo.ResourceVO;
import org.limbo.doorkeeper.server.dao.ClientMapper;
import org.limbo.doorkeeper.server.dao.ResourceMapper;
import org.limbo.doorkeeper.server.dao.ResourceTagMapper;
import org.limbo.doorkeeper.server.dao.ResourceUriMapper;
import org.limbo.doorkeeper.server.entity.Client;
import org.limbo.doorkeeper.server.entity.Resource;
import org.limbo.doorkeeper.server.entity.ResourceTag;
import org.limbo.doorkeeper.server.entity.ResourceUri;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @date 2021/1/5 4:59 下午
 */
@Service
public class ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private ResourceUriMapper resourceUriMapper;

    @Autowired
    private ResourceTagMapper resourceTagMapper;

    @Transactional
    public ResourceVO add(Long realmId, Long clientId, ResourceAddParam param) {
        Client client = clientMapper.getById(realmId, clientId);
        Verifies.notNull(client, "委托方不存在");

        Resource resource = EnhancedBeanUtils.createAndCopy(param, Resource.class);
        resource.setRealmId(client.getRealmId());
        resource.setClientId(client.getClientId());
        try {
            resourceMapper.insert(resource);
        } catch (DuplicateKeyException e) {
            throw new ParamException("资源已存在");
        }

        // 资源uri
        batchSaveUri(resource.getResourceId(), resource.getRealmId(), resource.getClientId(), param.getUris());
        // 资源标签
        batchSaveTag(resource.getResourceId(), resource.getRealmId(), resource.getClientId(), param.getTags());

        return EnhancedBeanUtils.createAndCopy(resource, ResourceVO.class);
    }

    @Transactional
    public void update(Long realmId, Long clientId, Long resourceId, ResourceUpdateParam param) {
        Resource resource = resourceMapper.getById(realmId, clientId, resourceId);
        Verifies.notNull(resource, "资源不存在");

        // 更新
        resourceMapper.update(null, Wrappers.<Resource>lambdaUpdate()
                .set(param.getDescription() != null, Resource::getDescription, param.getDescription())
                .set(param.getIsEnabled() != null, Resource::getIsEnabled, param.getIsEnabled())
                .eq(Resource::getResourceId, resourceId)
        );

        // 删除uri
        List<Long> uriIds = param.getUris().stream()
                .map(ResourceUriAddParam::getResourceUriId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        resourceUriMapper.delete(Wrappers.<ResourceUri>lambdaQuery()
                .notIn(CollectionUtils.isNotEmpty(uriIds), ResourceUri::getResourceUriId, uriIds)
        );
        // 新增uri
        List<ResourceUriAddParam> uriParams = param.getUris().stream()
                .filter(uri -> uri.getResourceUriId() == null)
                .collect(Collectors.toList());
        batchSaveUri(resource.getResourceId(), resource.getRealmId(), resource.getClientId(), uriParams);
        // 删除tag
        List<Long> tagIds = param.getTags().stream()
                .map(ResourceTagAddParam::getResourceTagId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        resourceTagMapper.delete(Wrappers.<ResourceTag>lambdaQuery()
                .notIn(CollectionUtils.isNotEmpty(tagIds), ResourceTag::getResourceTagId, tagIds)
        );
        // 新增tag
        List<ResourceTagAddParam> tagParams = param.getTags().stream()
                .filter(tag -> tag.getResourceTagId() == null)
                .collect(Collectors.toList());
        batchSaveTag(resource.getResourceId(), resource.getRealmId(), resource.getClientId(), tagParams);
    }

    @Transactional
    public void batchUpdate(Long realmId, Long clientId, ResourceBatchUpdateParam param) {
        switch (param.getType()) {
            case UPDATE:
                resourceMapper.update(null, Wrappers.<Resource>lambdaUpdate()
                        .set(param.getIsEnabled() != null, Resource::getIsEnabled, param.getIsEnabled())
                        .in(Resource::getResourceId, param.getResourceIds())
                        .eq(Resource::getRealmId, realmId)
                        .eq(Resource::getClientId, clientId)
                );
            default:
                break;
        }
    }

    public ResourceVO get(Long realmId, Long clientId, Long resourceId) {
        Resource resource = resourceMapper.getById(realmId, clientId, resourceId);
        Verifies.notNull(resource, "资源不存在");
        List<ResourceTag> resourceTags = resourceTagMapper.selectList(Wrappers.<ResourceTag>lambdaQuery()
                .eq(ResourceTag::getResourceId, resourceId)
        );
        List<ResourceUri> resourceUris = resourceUriMapper.selectList(Wrappers.<ResourceUri>lambdaQuery()
                .eq(ResourceUri::getResourceId, resourceId)
        );
        ResourceVO result = EnhancedBeanUtils.createAndCopy(resource, ResourceVO.class);
        result.setUris(EnhancedBeanUtils.createAndCopyList(resourceUris, ResourceUriVO.class));
        result.setTags(EnhancedBeanUtils.createAndCopyList(resourceTags, ResourceTagVO.class));
        return result;
    }

    public Page<ResourceVO> page(Long realmId, Long clientId, ResourceQueryParam param) {
        param.setRealmId(realmId);
        param.setClientId(clientId);
        long count = resourceMapper.pageVOCount(param);
        param.setTotal(count);
        if (count > 0) {
            param.setData(resourceMapper.pageVOS(param));
        }
        return param;
    }

    private void batchSaveUri(Long resourceId, Long realmId, Long clientId, List<ResourceUriAddParam> params) {
        if (CollectionUtils.isNotEmpty(params)) {
            List<ResourceUri> uris = new ArrayList<>();
            for (ResourceUriAddParam uriParam : params) {
                ResourceUri uri = EnhancedBeanUtils.createAndCopy(uriParam, ResourceUri.class);
                uri.setResourceId(resourceId);
                uri.setRealmId(realmId);
                uri.setClientId(clientId);
                // todo dim
                uris.add(uri);
            }
            MyBatisPlusUtils.batchSave(uris, ResourceUri.class);
        }
    }

    private void batchSaveTag(Long resourceId, Long realmId, Long clientId, List<ResourceTagAddParam> params) {
        if (CollectionUtils.isNotEmpty(params)) {
            List<ResourceTag> tags = new ArrayList<>();
            for (ResourceTagAddParam tagParam : params) {
                ResourceTag tag = EnhancedBeanUtils.createAndCopy(tagParam, ResourceTag.class);
                tag.setResourceId(resourceId);
                tag.setRealmId(realmId);
                tag.setClientId(clientId);
                tags.add(tag);
            }
            MyBatisPlusUtils.batchSave(tags, ResourceTag.class);
        }
    }

}
