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
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.resource.*;
import org.limbo.doorkeeper.api.model.vo.ResourceVO;
import org.limbo.doorkeeper.server.dal.entity.*;
import org.limbo.doorkeeper.server.dal.mapper.*;
import org.limbo.doorkeeper.server.support.ParamException;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private PermissionResourceMapper permissionResourceMapper;

    @Autowired
    private ResourceUriMapper resourceUriMapper;

    @Autowired
    private ResourceTagMapper resourceTagMapper;

    @Autowired
    private PermissionResourceService permissionResourceService;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private UriMapper uriMapper;

    @Autowired
    private ResourceAssociationMapper resourceAssociationMapper;

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
        // 绑定资源到权限上
        bindPermissions(resource.getResourceId(), param.getPermissionIds());
        // 绑定资源关系
        bindResourceAssociation(resource.getResourceId(), param.getParentIds(), param.getParentNames());

        return EnhancedBeanUtils.createAndCopy(resource, ResourceVO.class);
    }

    @Transactional
    public void update(Long realmId, Long clientId, Long resourceId, ResourceUpdateParam param) {
        Resource resource = resourceMapper.getById(realmId, clientId, resourceId);
        Verifies.notNull(resource, "资源不存在");

        try {
            // 更新
            resourceMapper.update(null, Wrappers.<Resource>lambdaUpdate()
                    .set(StringUtils.isNotBlank(param.getName()) && !resource.getName().equals(param.getName()),
                            Resource::getName, param.getName())
                    .set(param.getDescription() != null, Resource::getDescription, param.getDescription())
                    .set(param.getIsEnabled() != null, Resource::getIsEnabled, param.getIsEnabled())
                    .eq(Resource::getResourceId, resourceId)
            );
        } catch (DuplicateKeyException e) {
            throw new ParamException("资源已存在");
        }

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

        // 删除资源关系
        resourceAssociationMapper.delete(Wrappers.<ResourceAssociation>lambdaQuery()
                .eq(ResourceAssociation::getResourceId, resourceId)
        );
        // 新增资源关系
        bindResourceAssociation(resource.getResourceId(), param.getParentIds(), param.getParentNames());
    }

    @Transactional
    public void batchUpdate(Long realmId, Long clientId, ResourceBatchUpdateParam param) {
        switch (param.getType()) {
            case UPDATE:
                if (CollectionUtils.isEmpty(param.getResourceIds())) {
                    return;
                }
                resourceMapper.update(null, Wrappers.<Resource>lambdaUpdate()
                        .set(param.getIsEnabled() != null, Resource::getIsEnabled, param.getIsEnabled())
                        .in(Resource::getResourceId, param.getResourceIds())
                        .eq(Resource::getRealmId, realmId)
                        .eq(Resource::getClientId, clientId)
                );
                break;
            case DELETE:
                if (CollectionUtils.isEmpty(param.getResourceIds())) {
                    return;
                }
                List<Resource> resources = resourceMapper.selectList(Wrappers.<Resource>lambdaQuery()
                        .select(Resource::getResourceId)
                        .eq(Resource::getRealmId, realmId)
                        .eq(Resource::getClientId, clientId)
                        .in(Resource::getResourceId, param.getResourceIds())
                );
                if (CollectionUtils.isEmpty(resources)) {
                    return;
                }
                List<Long> resourceIds = resources.stream().map(Resource::getResourceId).collect(Collectors.toList());
                resourceMapper.deleteBatchIds(resourceIds);
                permissionResourceMapper.delete(Wrappers.<PermissionResource>lambdaQuery()
                        .in(PermissionResource::getResourceId, resourceIds)
                );
                resourceTagMapper.delete(Wrappers.<ResourceTag>lambdaQuery()
                        .in(ResourceTag::getResourceId, resourceIds)
                );
                resourceUriMapper.delete(Wrappers.<ResourceUri>lambdaQuery()
                        .in(ResourceUri::getResourceId, resourceIds)
                );
                resourceAssociationMapper.delete(Wrappers.<ResourceAssociation>lambdaQuery()
                        .in(ResourceAssociation::getResourceId, resourceIds)
                );
                resourceAssociationMapper.delete(Wrappers.<ResourceAssociation>lambdaQuery()
                        .in(ResourceAssociation::getParentId, resourceIds)
                );
                break;
            default:
                break;
        }
    }

    public ResourceVO get(Long realmId, Long clientId, Long resourceId) {
        return resourceMapper.getVO(realmId, clientId, resourceId);
    }

    public Page<ResourceVO> page(Long realmId, Long clientId, ResourceQueryParam param) {
        param.setRealmId(realmId);
        param.setClientId(clientId);
        long count = resourceMapper.voCount(param);
        param.setTotal(count);
        if (count > 0) {
            param.setData(resourceMapper.getVOS(param));
        }
        return param;
    }

    private void batchSaveUri(Long resourceId, Long realmId, Long clientId, List<ResourceUriAddParam> params) {
        if (CollectionUtils.isEmpty(params)) {
            return;
        }

        // 新增并获取uri
        List<Uri> uris = new ArrayList<>();
        HashSet<String> uriP = new HashSet<>();
        for (ResourceUriAddParam uriParam : params) {
            Uri uri = new Uri();
            uri.setRealmId(realmId);
            uri.setClientId(clientId);
            uri.setMethod(uriParam.getMethod());
            uri.setUri(uriParam.getUri().trim());

            uris.add(uri);
            uriP.add(uriParam.getUri().trim());
        }
        uriMapper.batchInsertIgnore(uris);

        uris = uriMapper.selectList(Wrappers.<Uri>lambdaQuery()
                .eq(Uri::getRealmId, realmId)
                .eq(Uri::getClientId, clientId)
                .in(Uri::getUri, uriP)
        );

        List<ResourceUri> resourceUris = new ArrayList<>();
        for (ResourceUriAddParam uriParam : params) {
            for (Uri uri : uris) {
                if (uri.getUri().trim().equals(uriParam.getUri().trim()) && uri.getMethod() == uriParam.getMethod()) {
                    ResourceUri resourceUri = new ResourceUri();
                    resourceUri.setResourceId(resourceId);
                    resourceUri.setUriId(uri.getUriId());
                    resourceUris.add(resourceUri);
                    break;
                }
            }
        }

        MyBatisPlusUtils.batchSave(resourceUris, ResourceUri.class);
    }

    private void batchSaveTag(Long resourceId, Long realmId, Long clientId, List<ResourceTagAddParam> params) {
        if (CollectionUtils.isEmpty(params)) {
            return;
        }

        // 新增并获取标签
        List<Tag> tags = new ArrayList<>();
        HashSet<String> kvs = new HashSet<>();
        for (ResourceTagAddParam tagParam : params) {
            Tag tag = new Tag();
            tag.setRealmId(realmId);
            tag.setClientId(clientId);
            tag.setK(tagParam.getK().trim());
            tag.setV(tagParam.getV().trim());
            String kv = tag.getK() + DoorkeeperConstants.KV_DELIMITER + tag.getV();
            tag.setKv(kv);

            tags.add(tag);
            kvs.add(kv);
        }
        tagMapper.batchInsertIgnore(tags);

        tags = tagMapper.selectList(Wrappers.<Tag>lambdaQuery()
                .eq(Tag::getRealmId, realmId)
                .eq(Tag::getClientId, clientId)
                .in(Tag::getKv, kvs)
        );

        List<ResourceTag> resourceTags = new ArrayList<>();
        for (ResourceTagAddParam tagParam : params) {
            for (Tag tag : tags) {
                if (tag.getK().trim().equals(tagParam.getK().trim()) && tag.getV().trim().equals(tagParam.getV().trim())) {
                    ResourceTag resourceTag = new ResourceTag();
                    resourceTag.setResourceId(resourceId);
                    resourceTag.setTagId(tag.getTagId());

                    resourceTags.add(resourceTag);
                    break;
                }
            }
        }

        MyBatisPlusUtils.batchSave(resourceTags, ResourceTag.class);
    }


    private void bindPermissions(Long resourceId, List<Long> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return;
        }
        List<PermissionResource> params = new ArrayList<>();
        for (Long permissionId : permissionIds) {
            PermissionResource addParam = new PermissionResource();
            addParam.setPermissionId(permissionId);
            addParam.setResourceId(resourceId);
            params.add(addParam);
        }
        permissionResourceService.batchSave(params);
    }

    private void bindResourceAssociation(Long resourceId, List<Long> parentIds, List<String> parentNames) {
        if (CollectionUtils.isEmpty(parentIds)) {
            parentIds = new ArrayList<>();
        }

        // 找出 parentNames 的 资源ID
        if (CollectionUtils.isNotEmpty(parentNames)) {
            List<Resource> resources = resourceMapper.selectList(Wrappers.<Resource>lambdaQuery()
                    .in(Resource::getName, parentNames)
            );
            if (CollectionUtils.isNotEmpty(resources)) {
                parentIds.addAll(resources.stream().map(Resource::getResourceId).collect(Collectors.toList()));
            }
        }

        List<ResourceAssociation> params = new ArrayList<>();
        for (Long parentId : parentIds) {
            if (resourceId.equals(parentId)) {
                // 防止出现自环
                continue;
            }
            ResourceAssociation model = new ResourceAssociation();
            model.setParentId(parentId);
            model.setResourceId(resourceId);
            params.add(model);
        }
        if (CollectionUtils.isEmpty(params)) {
            return;
        }
        resourceAssociationMapper.batchInsertIgnore(params);

    }

}
