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

package org.limbo.doorkeeper.server.support.authc;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.model.param.auth.AuthenticationUriCheckParam;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.limbo.doorkeeper.server.dao.ClientMapper;
import org.limbo.doorkeeper.server.dao.PermissionResourceMapper;
import org.limbo.doorkeeper.server.dao.ResourceMapper;
import org.limbo.doorkeeper.server.dao.ResourceUriMapper;
import org.limbo.doorkeeper.server.entity.Client;
import org.limbo.doorkeeper.server.entity.PermissionResource;
import org.limbo.doorkeeper.server.entity.Resource;
import org.limbo.doorkeeper.server.entity.ResourceUri;
import org.limbo.doorkeeper.server.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @date 2021/1/14 10:31 上午
 */
@Component
public class UriAllowExecutor extends AbstractAllowedExecutor<AuthenticationUriCheckParam, String> {

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private ResourceUriMapper resourceUriMapper;

    @Autowired
    private PermissionResourceMapper permissionResourceMapper;

    @Autowired
    private PermissionService permissionService;

    @Override
    public Map<Intention, List<String>> accessAllowed(Long userId, Long clientId, AuthenticationUriCheckParam param) {
        // 获取对应的client
        Client client = clientMapper.selectById(clientId);

        Map<Intention, List<String>> result = new HashMap<>();
        result.put(Intention.ALLOW, new ArrayList<>());
        result.put(Intention.REFUSE, new ArrayList<>());

        if (CollectionUtils.isEmpty(param.getUris())) {
            return result;
        }

        // 获取client下所有uri资源
        List<ResourceUri> uris = resourceUriMapper.selectList(Wrappers.<ResourceUri>lambdaQuery()
                .eq(ResourceUri::getRealmId, client.getRealmId())
                .eq(ResourceUri::getClientId, client.getClientId())
        );

        if (CollectionUtils.isEmpty(uris)) {
            return result;
        }


        for (String uri : param.getUris()) {

            // 由于uri可能会匹配到多个资源 最终如果一个资源被拦截了 就拦截 否则满足一个放行才放行

            Set<Long> resourceIds = new HashSet<>();
            for (ResourceUri resourceUri : uris) {
                if (pathMatch(resourceUri.getUri(), uri)) {
                    resourceIds.add(resourceUri.getResourceId());
                }
            }

            Intention intention = Intention.REFUSE;
            for (Long resourceId : resourceIds) {
                Resource resource = resourceMapper.selectById(resourceId);
                if (resource == null) {
                    continue;
                }

                List<PermissionResource> permissionResources = permissionResourceMapper.selectList(Wrappers.<PermissionResource>lambdaQuery()
                        .eq(PermissionResource::getResourceId, resource.getResourceId())
                );
                if (CollectionUtils.isEmpty(permissionResources)) {
                    continue;
                }

                Set<Long> permissionIds = permissionResources.stream().map(PermissionResource::getPermissionId).collect(Collectors.toSet());
                // 找到权限以及对应的策略
                List<PermissionVO> permissions = new ArrayList<>();
                for (Long permissionId : permissionIds) {
                    PermissionVO permissionVO = permissionService.get(client.getRealmId(), client.getClientId(), permissionId);
                    if (permissionVO != null) {
                        permissions.add(permissionVO);
                    }
                }
                if (CollectionUtils.isEmpty(permissions)) {
                    continue;
                }
                Map<Intention, Set<PermissionVO>> intentionPermissions = permissions.stream().collect(Collectors.groupingBy(
                        permissionVO -> Intention.parse(permissionVO.getIntention()),
                        Collectors.mapping(p -> p, Collectors.toSet())
                ));

                // 找到拦截的权限 如果满足 返回false
                Set<PermissionVO> refuse = intentionPermissions.get(Intention.REFUSE);
                if (CollectionUtils.isNotEmpty(refuse)) {
                    boolean r = false;
                    for (PermissionVO permissionVO : refuse) {
                        if (permissionExecute(userId, client.getRealmId(), client.getClientId(), permissionVO, param.getParams())) {
                            r = true;
                            break;
                        }
                    }
                    if (r) {
                        intention = Intention.REFUSE;
                        break;
                    }
                }


                // 找到放行的权限 如果满足 返回true
                Set<PermissionVO> allow = intentionPermissions.get(Intention.ALLOW);
                if (CollectionUtils.isNotEmpty(allow)) {
                    boolean a = false;
                    for (PermissionVO permissionVO : allow) {
                        if (permissionExecute(userId, client.getRealmId(), client.getClientId(), permissionVO, param.getParams())) {
                            a = true;
                            break;
                        }
                    }
                    if (a) {
                        intention = Intention.ALLOW;
                    }
                }

            }

            result.get(intention).add(uri);

        }

        return result;
    }

}