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
import org.limbo.doorkeeper.api.constants.BatchMethod;
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.api.constants.PolicyType;
import org.limbo.doorkeeper.api.model.param.permission.PermissionAddParam;
import org.limbo.doorkeeper.api.model.param.permission.PermissionPolicyAddParam;
import org.limbo.doorkeeper.api.model.param.permission.PermissionResourceAddParam;
import org.limbo.doorkeeper.api.model.param.policy.PolicyAddParam;
import org.limbo.doorkeeper.api.model.param.policy.PolicyRoleAddParam;
import org.limbo.doorkeeper.api.model.param.resource.ResourceAddParam;
import org.limbo.doorkeeper.api.model.param.resource.ResourceTagAddParam;
import org.limbo.doorkeeper.api.model.param.resource.ResourceUriAddParam;
import org.limbo.doorkeeper.api.model.param.role.RoleAddParam;
import org.limbo.doorkeeper.api.model.param.user.UserRoleBatchUpdateParam;
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
    private UserRoleService userRoleService;

    /**
     * @param userId    创建者ID
     * @param realmId   新建的RealmId
     * @param realmName 新建的realm名称
     */
    @Transactional
    public void createRealmData(Long userId, Long realmId, String realmName) {
        Realm dkRealm = realmMapper.selectOne(Wrappers.<Realm>lambdaQuery()
                .eq(Realm::getName, DoorkeeperConstants.REALM_NAME)
        );

        Client client = new Client();
        client.setRealmId(dkRealm.getRealmId());
        client.setName(realmName);
        client.setIsEnabled(true);
        clientMapper.insert(client);

        // 域
        ResourceAddParam realmResourceParam = createRealmResource(realmId);
        ResourceVO realmResource = resourceService.add(client.getRealmId(), client.getClientId(), realmResourceParam);

        RoleAddParam realmAdminRoleParam = createRole("域管理员", "拥有域下所有权限");
        RoleVO realmAdminRole = roleService.add(client.getRealmId(), client.getClientId(), realmAdminRoleParam);

        PolicyAddParam realmAdminPolicyParam = createRolePolicy("放行域管理员", realmAdminRole.getRoleId());
        PolicyVO realmAdminPolicy = policyService.add(client.getRealmId(), client.getClientId(), realmAdminPolicyParam);

        PermissionAddParam realmAdminPermissionParam = createPermission("域管理", realmResource.getResourceId(), realmAdminPolicy.getPolicyId());
        permissionService.add(client.getRealmId(), client.getClientId(), realmAdminPermissionParam);

        // 增加角色 用于域的绑定
        RoleAddParam realmBindParam = createRole(DoorkeeperConstants.BINDER, "绑定域用于查询");
        RoleVO realmBind = roleService.add(client.getRealmId(), client.getClientId(), realmBindParam);

        // 绑定用户角色
        List<Long> roleIds = new ArrayList<>();
        roleIds.add(realmAdminRole.getRoleId());
        roleIds.add(realmBind.getRoleId());
        UserRoleBatchUpdateParam userRoleBatchUpdateParam = new UserRoleBatchUpdateParam();
        userRoleBatchUpdateParam.setType(BatchMethod.SAVE);
        userRoleBatchUpdateParam.setRoleIds(roleIds);
        userRoleService.batchUpdate(client.getRealmId(), userId, userRoleBatchUpdateParam);


        // 委托方
        ResourceAddParam realmClientResourceParam = createRealmClientResource(realmId);
        ResourceVO realmClientResource = resourceService.add(client.getRealmId(), client.getClientId(), realmClientResourceParam);

        RoleAddParam realmClientManagerParam = createRole("委托方管理员", "委托方查看编辑");
        RoleVO realmClientManager = roleService.add(client.getRealmId(), client.getClientId(), realmClientManagerParam);

        PolicyAddParam realmClientPolicyParam = createRolePolicy("放行委托方管理员", realmClientManager.getRoleId());
        PolicyVO realmClientPolicy = policyService.add(client.getRealmId(), client.getClientId(), realmClientPolicyParam);

        PermissionAddParam realmClientPermissionParam = createPermission("委托方管理", realmClientResource.getResourceId(), realmClientPolicy.getPolicyId());
        permissionService.add(client.getRealmId(), client.getClientId(), realmClientPermissionParam);

        // 域角色
        ResourceAddParam realmRoleResourceParam = createRealmRoleResource(realmId);
        ResourceVO realmRoleResource = resourceService.add(client.getRealmId(), client.getClientId(), realmRoleResourceParam);

        RoleAddParam realmRoleManagerParam = createRole("域角色管理员", "域角色查看编辑");
        RoleVO realmRoleManager = roleService.add(client.getRealmId(), client.getClientId(), realmRoleManagerParam);

        PolicyAddParam realmRolePolicyParam = createRolePolicy("放行域角色管理员", realmRoleManager.getRoleId());
        PolicyVO realmRolePolicy = policyService.add(client.getRealmId(), client.getClientId(), realmRolePolicyParam);

        PermissionAddParam realmRolePermissionParam = createPermission("域角色管理", realmRoleResource.getResourceId(), realmRolePolicy.getPolicyId());
        permissionService.add(client.getRealmId(), client.getClientId(), realmRolePermissionParam);

        // 用户
        ResourceAddParam userResourceParam = createUserResource(realmId);
        ResourceVO userResource = resourceService.add(client.getRealmId(), client.getClientId(), userResourceParam);

        RoleAddParam realmUserManagerParam = createRole("用户管理员", "用户查看编辑");
        RoleVO realmUserManager = roleService.add(client.getRealmId(), client.getClientId(), realmUserManagerParam);

        PolicyAddParam realmUserPolicyParam = createRolePolicy("放行用户管理员", realmUserManager.getRoleId());
        PolicyVO realmUserPolicy = policyService.add(client.getRealmId(), client.getClientId(), realmUserPolicyParam);

        PermissionAddParam realmUserPermissionParam = createPermission("用户管理", userResource.getResourceId(), realmUserPolicy.getPolicyId());
        permissionService.add(client.getRealmId(), client.getClientId(), realmUserPermissionParam);
    }

//    @Transactional
//    public void creatClientData(Long userId, Long realmId, Long clientId, String clientName) {
//        Realm dkRealm = realmMapper.selectOne(Wrappers.<Realm>lambdaQuery()
//                .eq(Realm::getName, DoorkeeperConstants.REALM_NAME)
//        );
//
//        Realm realm = realmMapper.selectById(realmId);
//
//        Client client = clientMapper.selectOne(Wrappers.<Client>lambdaQuery()
//                .eq(Client::getRealmId, dkRealm.getRealmId())
//                .eq(Client::getName, realm.getName())
//        );
//
//        ResourceAddParam clientResourceParam = createClientResource(client.getRealmId(), clientId, clientName);
//        ResourceVO clientResource = resourceService.add(client.getRealmId(), client.getClientId(), clientResourceParam);
//
//        RoleAddParam clientAdminRoleParam = createRole(clientName + "-管理员", "拥有委托方" + clientName + "所有权限");
//        RoleVO clientAdminRole = roleService.add(client.getRealmId(), client.getClientId(), clientAdminRoleParam);
//
//        PolicyAddParam clientAdminPolicyParam = createRolePolicy("放行" + clientName + "-管理员", clientAdminRole.getRoleId());
//        PolicyVO clientAdminPolicy = policyService.add(client.getRealmId(), client.getClientId(), clientAdminPolicyParam);
//
//        PermissionAddParam clientAdminPermissionParam = createPermission(clientName + "管理", clientResource.getResourceId(), clientAdminPolicy.getPolicyId());
//        permissionService.add(client.getRealmId(), client.getClientId(), clientAdminPermissionParam);
//
//        // 绑定用户角色
//        UserRoleBatchUpdateParam userRoleBatchUpdateParam = new UserRoleBatchUpdateParam();
//        userRoleBatchUpdateParam.setType(BatchMethod.SAVE);
//        userRoleBatchUpdateParam.setRoleIds(Collections.singletonList(clientAdminRole.getRoleId()));
//        userRoleService.batchUpdate(client.getRealmId(), userId, userRoleBatchUpdateParam);
//    }

    /**
     * @param realmId 新建的域的id
     */
    private ResourceAddParam createRealmResource(Long realmId) {
        ResourceAddParam resourceAddParam = new ResourceAddParam();
        resourceAddParam.setName("域");
        resourceAddParam.setIsEnabled(true);

        ResourceUriAddParam uriAddParam = new ResourceUriAddParam();
        uriAddParam.setUri("/admin/realm/" + realmId + "/**");

        resourceAddParam.setUris(Collections.singletonList(uriAddParam));

        ResourceTagAddParam realmIdTag = new ResourceTagAddParam();
        realmIdTag.setK(DoorkeeperConstants.REALM_ID);
        realmIdTag.setV(realmId + "");

        resourceAddParam.setTags(Collections.singletonList(realmIdTag));

        return resourceAddParam;
    }

//    /**
//     * @param realmId 新建的client的所在域的id
//     * @param clientId 新建的client的id
//     */
//    private ResourceAddParam createClientResource(Long realmId, Long clientId, String clientName) {
//        ResourceAddParam resourceAddParam = new ResourceAddParam();
//        resourceAddParam.setName("委托方-" + clientName);
//        resourceAddParam.setIsEnabled(true);
//
//        ResourceUriAddParam uriAddParam = new ResourceUriAddParam();
//        uriAddParam.setUri("/admin/realm/" + realmId + "/client/" + clientId + "/**");
//
//        resourceAddParam.setUris(Collections.singletonList(uriAddParam));
//
//        ResourceTagAddParam clientIdTag = new ResourceTagAddParam();
//        clientIdTag.setK(DoorkeeperConstants.CLIENT_ID);
//        clientIdTag.setV(clientId + "");
//
//        resourceAddParam.setTags(Collections.singletonList(clientIdTag));
//
//        return resourceAddParam;
//    }

    /**
     * @param realmId 新建的域的id
     */
    private ResourceAddParam createRealmRoleResource(Long realmId) {
        ResourceAddParam resourceAddParam = new ResourceAddParam();
        resourceAddParam.setName("域角色");
        resourceAddParam.setIsEnabled(true);

        ResourceUriAddParam uriAddParam = new ResourceUriAddParam();
        uriAddParam.setUri("/admin/realm/" + realmId + "/client/" + DoorkeeperConstants.REALM_CLIENT_DEFAULT_ID + "/role/**");

        resourceAddParam.setUris(Collections.singletonList(uriAddParam));

        return resourceAddParam;
    }

    /**
     * @param realmId 新建的域的id
     */
    private ResourceAddParam createRealmClientResource(Long realmId) {
        ResourceAddParam resourceAddParam = new ResourceAddParam();
        resourceAddParam.setName("委托方");
        resourceAddParam.setIsEnabled(true);

        ResourceUriAddParam uriAddParam = new ResourceUriAddParam();
        uriAddParam.setUri("/admin/realm/" + realmId + "/client/**");

        resourceAddParam.setUris(Collections.singletonList(uriAddParam));

        return resourceAddParam;
    }

    /**
     * @param realmId 新建的域的id
     */
    private ResourceAddParam createUserResource(Long realmId) {
        ResourceAddParam resourceAddParam = new ResourceAddParam();
        resourceAddParam.setName("用户");
        resourceAddParam.setIsEnabled(true);

        ResourceUriAddParam uriAddParam = new ResourceUriAddParam();
        uriAddParam.setUri("/admin/realm/" + realmId + "/user/**");

        resourceAddParam.setUris(Collections.singletonList(uriAddParam));

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
