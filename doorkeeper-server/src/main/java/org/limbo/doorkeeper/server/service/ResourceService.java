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
import org.limbo.doorkeeper.api.model.param.*;
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
    public ResourceVO add(ResourceAddParam param) {
        Client client = clientMapper.selectById(param.getClientId());
        Verifies.notNull(client, "委托方不存在");

        Resource resource = EnhancedBeanUtils.createAndCopy(param, Resource.class);
        resource.setRealmId(client.getRealmId());
        try {
            resourceMapper.insert(resource);
        } catch (DuplicateKeyException e) {
            throw new ParamException("资源已存在");
        }

        // 资源uri
        if (CollectionUtils.isNotEmpty(param.getUris())) {
            List<ResourceUri> uris = new ArrayList<>();
            for (ResourceUriAddParam uriParam : param.getUris()) {
                ResourceUri uri = EnhancedBeanUtils.createAndCopy(uriParam, ResourceUri.class);
                uri.setResourceId(resource.getResourceId());
                uris.add(uri);
            }
            MyBatisPlusUtils.batchSave(uris, ResourceUri.class);
        }
        // 资源标签
        if (CollectionUtils.isNotEmpty(param.getTags())) {
            List<ResourceTag> tags = new ArrayList<>();
            for (ResourceTagAddParam tagParam : param.getTags()) {
                ResourceTag tag = EnhancedBeanUtils.createAndCopy(tagParam, ResourceTag.class);
                tag.setResourceId(resource.getResourceId());
                tags.add(tag);
            }
            MyBatisPlusUtils.batchSave(tags, ResourceTag.class);
        }

        return EnhancedBeanUtils.createAndCopy(resource, ResourceVO.class);
    }

    @Transactional
    public void update(Long resourceId, ResourceUpdateParam param) {
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
        if (CollectionUtils.isNotEmpty(uriParams)) {
            List<ResourceUri> uris = new ArrayList<>();
            for (ResourceUriAddParam uriParam : uriParams) {
                ResourceUri uri = EnhancedBeanUtils.createAndCopy(uriParam, ResourceUri.class);
                uri.setResourceId(resourceId);
                uris.add(uri);
            }
            MyBatisPlusUtils.batchSave(uris, ResourceUri.class);
        }
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
        if (CollectionUtils.isNotEmpty(tagParams)) {
            List<ResourceTag> tags = new ArrayList<>();
            for (ResourceTagAddParam tagParam : tagParams) {
                ResourceTag tag = EnhancedBeanUtils.createAndCopy(tagParam, ResourceTag.class);
                tag.setResourceId(resourceId);
                tags.add(tag);
            }
            MyBatisPlusUtils.batchSave(tags, ResourceTag.class);
        }
    }

    @Transactional
    public void batchUpdate(ResourceBatchUpdateParam param) {
        switch (param.getType()) {
            case PUT:
                resourceMapper.update(null, Wrappers.<Resource>lambdaUpdate()
                        .set(param.getIsEnabled() != null, Resource::getIsEnabled, param.getIsEnabled())
                        .in(Resource::getResourceId, param.getResourceIds())
                );
            default:
                break;
        }
    }

    public ResourceVO get(Long resourceId) {
        Resource resource = resourceMapper.selectById(resourceId);
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

    public Page<ResourceVO> page(ResourceQueryParam param) {
        long count = resourceMapper.pageVOCount(param);
        param.setTotal(count);
        if (count > 0) {
            param.setData(resourceMapper.pageVOS(param));
        }
        return param;
    }

}
