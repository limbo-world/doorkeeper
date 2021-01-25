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
import org.limbo.doorkeeper.server.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.server.dal.mapper.ClientMapper;
import org.limbo.doorkeeper.server.dal.mapper.ResourceMapper;
import org.limbo.doorkeeper.server.dal.mapper.ResourceTagMapper;
import org.limbo.doorkeeper.server.dal.mapper.ResourceUriMapper;
import org.limbo.doorkeeper.server.dal.entity.Client;
import org.limbo.doorkeeper.server.dal.entity.Resource;
import org.limbo.doorkeeper.server.dal.entity.ResourceTag;
import org.limbo.doorkeeper.server.dal.entity.ResourceUri;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        resourceUriMapper.delete(Wrappers.<ResourceUri>lambdaQuery()
                .eq(ResourceUri::getResourceId, resourceId)
        );
        // 新增uri
        batchSaveUri(resource.getResourceId(), resource.getRealmId(), resource.getClientId(), param.getUris());
        // 删除tag
        resourceTagMapper.delete(Wrappers.<ResourceTag>lambdaQuery()
                .eq(ResourceTag::getResourceId, resourceId)
        );
        // 新增tag
        batchSaveTag(resource.getResourceId(), resource.getRealmId(), resource.getClientId(), param.getTags());
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
                ResourceUri uri = new ResourceUri();
                uri.setUri(uriParam.getUri());
                uri.setResourceId(resourceId);
                uri.setRealmId(realmId);
                uri.setClientId(clientId);
                uris.add(uri);
            }
            MyBatisPlusUtils.batchSave(uris, ResourceUri.class);
        }
    }

    private void batchSaveTag(Long resourceId, Long realmId, Long clientId, List<ResourceTagAddParam> params) {
        if (CollectionUtils.isNotEmpty(params)) {
            List<ResourceTag> tags = new ArrayList<>();
            for (ResourceTagAddParam tagParam : params) {
                ResourceTag tag = new ResourceTag();
                tag.setK(tagParam.getK());
                tag.setV(tagParam.getV());
                tag.setKv(tagParam.getK() + DoorkeeperConstants.KV_DELIMITER + tagParam.getV());
                tag.setResourceId(resourceId);
                tag.setRealmId(realmId);
                tag.setClientId(clientId);
                tags.add(tag);
            }
            MyBatisPlusUtils.batchSave(tags, ResourceTag.class);
        }
    }



}
