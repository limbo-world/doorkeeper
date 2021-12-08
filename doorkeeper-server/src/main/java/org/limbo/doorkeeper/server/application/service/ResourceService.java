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

package org.limbo.doorkeeper.server.application.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.constants.ApiConstants;
import org.limbo.doorkeeper.api.dto.param.add.ResourceAddParam;
import org.limbo.doorkeeper.api.dto.param.add.ResourceTagAddParam;
import org.limbo.doorkeeper.api.dto.param.add.ResourceUriAddParam;
import org.limbo.doorkeeper.api.dto.param.batch.ResourceBatchUpdateParam;
import org.limbo.doorkeeper.api.dto.param.query.ResourceQueryParam;
import org.limbo.doorkeeper.api.dto.param.update.ResourceUpdateParam;
import org.limbo.doorkeeper.api.dto.vo.PageVO;
import org.limbo.doorkeeper.api.dto.vo.ResourceVO;
import org.limbo.doorkeeper.server.infrastructure.dao.PermissionResourceDao;
import org.limbo.doorkeeper.server.infrastructure.exception.ParamException;
import org.limbo.doorkeeper.server.infrastructure.mapper.*;
import org.limbo.doorkeeper.server.infrastructure.po.*;
import org.limbo.doorkeeper.server.infrastructure.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.infrastructure.utils.MyBatisPlusUtils;
import org.limbo.doorkeeper.server.infrastructure.utils.Verifies;
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
 * @since 2021/1/5 4:59 下午
 */
@Service
public class ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private NamespaceMapper namespaceMapper;

    @Autowired
    private PermissionResourceMapper permissionResourceMapper;

    @Autowired
    private ResourceUriMapper resourceUriMapper;

    @Autowired
    private ResourceTagMapper resourceTagMapper;

    @Autowired
    private PermissionResourceDao permissionResourceDao;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private UriMapper uriMapper;

    @Autowired
    private ResourceAssociationMapper resourceAssociationMapper;

    @Transactional
    public ResourceVO add(Long realmId, Long clientId, ResourceAddParam param) {
        NamespacePO client = namespaceMapper.getById(realmId, clientId);
        Verifies.notNull(client, "委托方不存在");

        ResourcePO resource = EnhancedBeanUtils.createAndCopy(param, ResourcePO.class);
        resource.setRealmId(client.getRealmId());
        resource.setNamespaceId(client.getNamespaceId());
        try {
            resourceMapper.insert(resource);
        } catch (DuplicateKeyException e) {
            throw new ParamException("资源已存在");
        }

        // 资源uri
        batchSaveUri(resource.getResourceId(), resource.getRealmId(), resource.getNamespaceId(), param.getUris());
        // 资源标签
        batchSaveTag(resource.getResourceId(), resource.getRealmId(), resource.getNamespaceId(), param.getTags());
        // 绑定资源到权限上
        bindPermissions(resource.getResourceId(), param.getPermissionIds());
        // 绑定资源关系
        bindResourceAssociation(resource.getResourceId(), param.getParentIds(), param.getParentNames());

        return EnhancedBeanUtils.createAndCopy(resource, ResourceVO.class);
    }

    @Transactional
    public void update(Long realmId, Long clientId, Long resourceId, ResourceUpdateParam param) {
        ResourcePO resource = resourceMapper.getById(realmId, clientId, resourceId);
        Verifies.notNull(resource, "资源不存在");

        try {
            // 更新
            resourceMapper.update(null, Wrappers.<ResourcePO>lambdaUpdate()
                    .set(StringUtils.isNotBlank(param.getName()) && !resource.getName().equals(param.getName()),
                            ResourcePO::getName, param.getName())
                    .set(param.getDescription() != null, ResourcePO::getDescription, param.getDescription())
                    .set(param.getIsEnabled() != null, ResourcePO::getIsEnabled, param.getIsEnabled())
                    .eq(ResourcePO::getResourceId, resourceId)
            );
        } catch (DuplicateKeyException e) {
            throw new ParamException("资源已存在");
        }

        // 删除uri
        resourceUriMapper.delete(Wrappers.<ResourceUriPO>lambdaQuery()
                .eq(ResourceUriPO::getResourceId, resourceId)
        );
        // 新增uri
        batchSaveUri(resource.getResourceId(), resource.getRealmId(), resource.getNamespaceId(), param.getUris());

        // 删除tag
        resourceTagMapper.delete(Wrappers.<ResourceTagPO>lambdaQuery()
                .eq(ResourceTagPO::getResourceId, resourceId)
        );
        // 新增tag
        batchSaveTag(resource.getResourceId(), resource.getRealmId(), resource.getNamespaceId(), param.getTags());

        // 删除资源关系
        resourceAssociationMapper.delete(Wrappers.<ResourceAssociationPO>lambdaQuery()
                .eq(ResourceAssociationPO::getResourceId, resourceId)
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
                resourceMapper.update(null, Wrappers.<ResourcePO>lambdaUpdate()
                        .set(param.getIsEnabled() != null, ResourcePO::getIsEnabled, param.getIsEnabled())
                        .in(ResourcePO::getResourceId, param.getResourceIds())
                        .eq(ResourcePO::getRealmId, realmId)
                        .eq(ResourcePO::getNamespaceId, clientId)
                );
                break;
            case DELETE:
                if (CollectionUtils.isEmpty(param.getResourceIds())) {
                    return;
                }
                List<ResourcePO> resources = resourceMapper.selectList(Wrappers.<ResourcePO>lambdaQuery()
                        .select(ResourcePO::getResourceId)
                        .eq(ResourcePO::getRealmId, realmId)
                        .eq(ResourcePO::getNamespaceId, clientId)
                        .in(ResourcePO::getResourceId, param.getResourceIds())
                );
                if (CollectionUtils.isEmpty(resources)) {
                    return;
                }
                List<Long> resourceIds = resources.stream().map(ResourcePO::getResourceId).collect(Collectors.toList());
                resourceMapper.deleteBatchIds(resourceIds);
                permissionResourceMapper.delete(Wrappers.<PermissionResourcePO>lambdaQuery()
                        .in(PermissionResourcePO::getResourceId, resourceIds)
                );
                resourceTagMapper.delete(Wrappers.<ResourceTagPO>lambdaQuery()
                        .in(ResourceTagPO::getResourceId, resourceIds)
                );
                resourceUriMapper.delete(Wrappers.<ResourceUriPO>lambdaQuery()
                        .in(ResourceUriPO::getResourceId, resourceIds)
                );
                resourceAssociationMapper.delete(Wrappers.<ResourceAssociationPO>lambdaQuery()
                        .in(ResourceAssociationPO::getResourceId, resourceIds)
                );
                resourceAssociationMapper.delete(Wrappers.<ResourceAssociationPO>lambdaQuery()
                        .in(ResourceAssociationPO::getParentId, resourceIds)
                );
                break;
            default:
                break;
        }
    }

    public ResourceVO get(Long realmId, Long clientId, Long resourceId) {
        return resourceMapper.getVO(realmId, clientId, resourceId);
    }

    public PageVO<ResourceVO> page(Long realmId, Long clientId, ResourceQueryParam param) {
        param.setRealmId(realmId);
        param.setClientId(clientId);
        long count = resourceMapper.voCount(param);

        PageVO<ResourceVO> result = PageVO.convertByPage(param);
        result.setTotal(count);
        if (count > 0) {
            result.setData(resourceMapper.getVOS(param));
        }
        return result;
    }

    /**
     * 同时保存uri数据和resource_uri关系
     *
     * @param resourceId
     * @param realmId
     * @param clientId
     * @param params
     */
    private void batchSaveUri(Long resourceId, Long realmId, Long clientId, List<ResourceUriAddParam> params) {
        if (CollectionUtils.isEmpty(params)) {
            return;
        }

        // 新增并获取uri
        List<UriPO> uris = new ArrayList<>();
        HashSet<String> uriP = new HashSet<>();
        for (ResourceUriAddParam uriParam : params) {
            UriPO uri = new UriPO();
            uri.setRealmId(realmId);
            uri.setClientId(clientId);
            uri.setMethod(uriParam.getMethod());
            uri.setUri(uriParam.getUri().trim());

            uris.add(uri);
            uriP.add(uriParam.getUri().trim());
        }
        uriMapper.batchInsertIgnore(uris);

        // 查询 uri 获取需要绑定的 id
        uris = uriMapper.selectList(Wrappers.<UriPO>lambdaQuery()
                .eq(UriPO::getRealmId, realmId)
                .eq(UriPO::getClientId, clientId)
                .in(UriPO::getUri, uriP)
        );

        List<ResourceUriPO> resourceUris = new ArrayList<>();
        for (ResourceUriAddParam uriParam : params) {
            for (UriPO uri : uris) {
                // 比如uri的方法是否一致 防止添加路径相同但是方法不同的数据
                if (uri.getUri().trim().equals(uriParam.getUri().trim()) && uri.getMethod() == uriParam.getMethod()) {
                    ResourceUriPO resourceUri = new ResourceUriPO();
                    resourceUri.setResourceId(resourceId);
                    resourceUri.setUriId(uri.getUriId());
                    resourceUris.add(resourceUri);
                    break;
                }
            }
        }

        MyBatisPlusUtils.batchSave(resourceUris, ResourceUriPO.class);
    }

    /**
     * 同时保存tag数据和resource_tag关系
     *
     * @param resourceId
     * @param realmId
     * @param clientId
     * @param params
     */
    private void batchSaveTag(Long resourceId, Long realmId, Long clientId, List<ResourceTagAddParam> params) {
        if (CollectionUtils.isEmpty(params)) {
            return;
        }

        // 新增并获取标签
        List<TagPO> tags = new ArrayList<>();
        HashSet<String> kvs = new HashSet<>();
        for (ResourceTagAddParam tagParam : params) {
            TagPO tag = new TagPO();
            tag.setRealmId(realmId);
            tag.setNamespaceId(clientId);
            tag.setKey(tagParam.getK().trim());
            tag.setValue(tagParam.getV().trim());
            String kv = tag.getKey() + ApiConstants.KV_DELIMITER + tag.getValue();
            tag.setKeyValue(kv);

            tags.add(tag);
            kvs.add(kv);
        }
        tagMapper.batchInsertIgnore(tags);

        // 查询 tag 获取需要绑定的 id
        tags = tagMapper.selectList(Wrappers.<TagPO>lambdaQuery()
                .eq(TagPO::getRealmId, realmId)
                .eq(TagPO::getNamespaceId, clientId)
                .in(TagPO::getKeyValue, kvs)
        );

        List<ResourceTagPO> resourceTags = new ArrayList<>();
        for (TagPO tag : tags) {
            ResourceTagPO resourceTag = new ResourceTagPO();
            resourceTag.setResourceId(resourceId);
            resourceTag.setTagId(tag.getTagId());
            resourceTags.add(resourceTag);
        }
        MyBatisPlusUtils.batchSave(resourceTags, ResourceTagPO.class);
    }


    private void bindPermissions(Long resourceId, List<Long> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return;
        }
        List<PermissionResourcePO> params = new ArrayList<>();
        for (Long permissionId : permissionIds) {
            PermissionResourcePO addParam = new PermissionResourcePO();
            addParam.setPermissionId(permissionId);
            addParam.setResourceId(resourceId);
            params.add(addParam);
        }
        permissionResourceDao.batchSave(params);
    }

    /**
     * 绑定资源关联关系
     *
     * @param resourceId
     * @param parentIds
     * @param parentNames
     */
    private void bindResourceAssociation(Long resourceId, List<Long> parentIds, List<String> parentNames) {
        if (CollectionUtils.isEmpty(parentIds)) {
            parentIds = new ArrayList<>();
        }

        // 找出 parentNames 的 资源ID
        if (CollectionUtils.isNotEmpty(parentNames)) {
            List<ResourcePO> resources = resourceMapper.selectList(Wrappers.<ResourcePO>lambdaQuery()
                    .in(ResourcePO::getName, parentNames)
            );
            if (CollectionUtils.isNotEmpty(resources)) {
                parentIds.addAll(resources.stream().map(ResourcePO::getResourceId).collect(Collectors.toList()));
            }
        }

        List<ResourceAssociationPO> params = new ArrayList<>();
        for (Long parentId : parentIds) {
            if (resourceId.equals(parentId)) {
                // 防止出现自环
                continue;
            }
            ResourceAssociationPO model = new ResourceAssociationPO();
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
