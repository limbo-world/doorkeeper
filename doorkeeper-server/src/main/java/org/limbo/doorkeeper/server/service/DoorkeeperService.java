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

import org.limbo.doorkeeper.api.constants.BatchMethod;
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.api.constants.PolicyType;
import org.limbo.doorkeeper.api.model.param.group.GroupAddParam;
import org.limbo.doorkeeper.api.model.param.group.GroupRoleBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.group.GroupUserBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.permission.PermissionAddParam;
import org.limbo.doorkeeper.api.model.param.permission.PermissionPolicyAddParam;
import org.limbo.doorkeeper.api.model.param.permission.PermissionResourceAddParam;
import org.limbo.doorkeeper.api.model.param.policy.PolicyAddParam;
import org.limbo.doorkeeper.api.model.param.policy.PolicyRoleAddParam;
import org.limbo.doorkeeper.api.model.param.resource.ResourceAddParam;
import org.limbo.doorkeeper.api.model.param.resource.ResourceTagAddParam;
import org.limbo.doorkeeper.api.model.param.resource.ResourceUriAddParam;
import org.limbo.doorkeeper.api.model.param.role.RoleAddParam;
import org.limbo.doorkeeper.api.model.vo.GroupVO;
import org.limbo.doorkeeper.api.model.vo.ResourceVO;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.server.dao.ClientMapper;
import org.limbo.doorkeeper.server.dao.RealmMapper;
import org.limbo.doorkeeper.server.entity.Client;
import org.limbo.doorkeeper.server.entity.Realm;
import org.limbo.doorkeeper.server.service.policy.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/10 10:39 上午
 */
@Service
public class DoorkeeperService {

    @Autowired
    private RealmMapper realmMapper;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupRoleService groupRoleService;

    @Autowired
    private GroupUserService groupUserService;

    /**
     * @param userId    创建者ID
     * @param realmId   新建的RealmId
     * @param realmName 新建的realm名称
     */
    @Transactional
    public void createRealmData(Long userId, Long realmId, String realmName) {
        Realm dkRealm = realmMapper.getDoorkeeperRealm();

        Client client = new Client();
        client.setRealmId(dkRealm.getRealmId());
        client.setName(realmName);
        client.setIsEnabled(true);
        clientMapper.insert(client);

        // 域资源
        ResourceAddParam realmResourceParam = createRealmResource(realmId);
        ResourceVO realmResource = resourceService.add(client.getRealmId(), client.getClientId(), realmResourceParam);
        // 域管理员角色
        RoleAddParam realmAdminRoleParam = createRole(DoorkeeperConstants.ADMIN, "");
        RoleVO realmAdminRole = roleService.add(client.getRealmId(), client.getClientId(), realmAdminRoleParam);
        // 域管理员策略
        PolicyAddParam realmAdminPolicyParam = createRolePolicy(DoorkeeperConstants.ADMIN, realmAdminRole.getRoleId());
        PolicyVO realmAdminPolicy = policyService.add(client.getRealmId(), client.getClientId(), realmAdminPolicyParam);
        // 域管理员权限
        PermissionAddParam realmAdminPermissionParam = createPermission(DoorkeeperConstants.ADMIN, realmResource.getResourceId(), realmAdminPolicy.getPolicyId());
        permissionService.add(client.getRealmId(), client.getClientId(), realmAdminPermissionParam);
        // 增加用户组
        // 找到名为realm的用户组
        GroupVO realmGroup = groupService.getRealmGroup();
        if (realmGroup == null) {
            GroupAddParam groupAddParam = new GroupAddParam();
            groupAddParam.setName(realmName);
            groupAddParam.setParentId(DoorkeeperConstants.DEFAULT_ID);
            realmGroup = groupService.add(dkRealm.getRealmId(), groupAddParam);
        }
        GroupAddParam groupAddParam = new GroupAddParam();
        groupAddParam.setName(realmName);
        groupAddParam.setParentId(realmGroup.getGroupId());
        GroupVO group = groupService.add(dkRealm.getRealmId(), groupAddParam);
        // 用户组绑定域管理员角色
        GroupRoleBatchUpdateParam groupRoleBatchUpdateParam = new GroupRoleBatchUpdateParam();
        groupRoleBatchUpdateParam.setType(BatchMethod.SAVE);
        groupRoleBatchUpdateParam.setRoleIds(Collections.singletonList(realmAdminRole.getRoleId()));
        groupRoleService.batchUpdate(dkRealm.getRealmId(), group.getGroupId(), groupRoleBatchUpdateParam);
        // 用户加入用户组
        GroupUserBatchUpdateParam dkRealmUserGroupParam = new GroupUserBatchUpdateParam();
        dkRealmUserGroupParam.setType(BatchMethod.SAVE);
        dkRealmUserGroupParam.setUserIds(Collections.singletonList(userId));
        groupUserService.batchUpdate(group.getGroupId(), dkRealmUserGroupParam);
    }

    /**
     * @param realmId 新建的域的id
     */
    private ResourceAddParam createRealmResource(Long realmId) {
        ResourceAddParam resourceAddParam = new ResourceAddParam();
        resourceAddParam.setName("realm");
        resourceAddParam.setIsEnabled(true);

        ResourceUriAddParam uriAddParam = new ResourceUriAddParam();
        uriAddParam.setUri("/admin/realm/" + realmId + "/**");

        resourceAddParam.setUris(Collections.singletonList(uriAddParam));

        ResourceTagAddParam realmIdTag = new ResourceTagAddParam();
        realmIdTag.setK(DoorkeeperConstants.REALM_ID);
        realmIdTag.setV(realmId + "");

        ResourceTagAddParam typeTag = new ResourceTagAddParam();
        typeTag.setK(DoorkeeperConstants.TYPE);
        typeTag.setV(DoorkeeperConstants.REALM);

        List<ResourceTagAddParam> tags = new ArrayList<>();
        tags.add(realmIdTag);
        tags.add(typeTag);

        resourceAddParam.setTags(tags);

        return resourceAddParam;
    }

    private RoleAddParam createRole(String name, String description) {
        RoleAddParam roleAddParam = new RoleAddParam();
        roleAddParam.setName(name);
        roleAddParam.setDescription(description);
        roleAddParam.setIsEnabled(true);
        return roleAddParam;
    }

    private PolicyAddParam createRolePolicy(String name, Long roleId) {
        PolicyAddParam policyAddParam = new PolicyAddParam();
        policyAddParam.setName(name);
        policyAddParam.setType(PolicyType.ROLE);
        policyAddParam.setLogic(Logic.ALL);
        policyAddParam.setIntention(Intention.ALLOW);
        policyAddParam.setIsEnabled(Boolean.TRUE);

        PolicyRoleAddParam roleAddParam = new PolicyRoleAddParam();
        roleAddParam.setRoleId(roleId);

        policyAddParam.setRoles(Collections.singletonList(roleAddParam));
        return policyAddParam;
    }

    private PermissionAddParam createPermission(String name, Long resourceId, Long policyId) {
        PermissionAddParam permissionAddParam = new PermissionAddParam();
        permissionAddParam.setName(name);
        permissionAddParam.setLogic(Logic.ALL);
        permissionAddParam.setIntention(Intention.ALLOW);
        permissionAddParam.setIsEnabled(Boolean.TRUE);

        PermissionResourceAddParam permissionResourceAddParam = new PermissionResourceAddParam();
        permissionResourceAddParam.setResourceId(resourceId);
        permissionAddParam.setResources(Collections.singletonList(permissionResourceAddParam));

        PermissionPolicyAddParam permissionPolicyAddParam = new PermissionPolicyAddParam();
        permissionPolicyAddParam.setPolicyId(policyId);
        permissionAddParam.setPolicys(Collections.singletonList(permissionPolicyAddParam));
        return permissionAddParam;
    }

}
