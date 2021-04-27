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

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.constants.*;
import org.limbo.doorkeeper.api.model.param.InitParam;
import org.limbo.doorkeeper.api.model.param.check.PolicyCheckerParam;
import org.limbo.doorkeeper.api.model.param.permission.PermissionAddParam;
import org.limbo.doorkeeper.api.model.param.permission.PermissionQueryParam;
import org.limbo.doorkeeper.api.model.param.permission.PermissionUpdateParam;
import org.limbo.doorkeeper.api.model.param.policy.PolicyAddParam;
import org.limbo.doorkeeper.api.model.param.policy.PolicyRoleAddParam;
import org.limbo.doorkeeper.api.model.param.policy.PolicyUserAddParam;
import org.limbo.doorkeeper.api.model.param.resource.ResourceAddParam;
import org.limbo.doorkeeper.api.model.param.resource.ResourceQueryParam;
import org.limbo.doorkeeper.api.model.param.role.RoleAddParam;
import org.limbo.doorkeeper.api.model.param.user.UserRoleBatchUpdateParam;
import org.limbo.doorkeeper.api.model.vo.*;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyRoleVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.dal.entity.*;
import org.limbo.doorkeeper.server.dal.entity.policy.Policy;
import org.limbo.doorkeeper.server.dal.mapper.*;
import org.limbo.doorkeeper.server.dal.mapper.policy.PolicyMapper;
import org.limbo.doorkeeper.server.service.policy.PolicyService;
import org.limbo.doorkeeper.server.support.ParamException;
import org.limbo.doorkeeper.server.support.auth.policies.PolicyCheckerFactory;
import org.limbo.doorkeeper.server.utils.JacksonUtil;
import org.limbo.doorkeeper.server.utils.MD5Utils;
import org.limbo.doorkeeper.server.utils.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * doorkeeper资源等管理逻辑
 *
 * @author Devil
 * @date 2021/1/10 10:39 上午
 */
@Slf4j
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
    private UserMapper userMapper;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PolicyCheckerFactory policyCheckerFactory;

    private String realmResource;

    private String clientResource;

    @Transactional
    public void initDoorkeeper(InitParam param) {
        Realm realm = new Realm();
        realm.setName(DoorkeeperConstants.DOORKEEPER_REALM_NAME);
        realm.setSecret(UUIDUtils.get());
        try {
            realmMapper.insert(realm);
        } catch (DuplicateKeyException e) {
            throw new ParamException("请勿重复操作");
        }

        // 创建管理员账户
        User admin = new User();
        admin.setRealmId(realm.getRealmId());
        admin.setNickname(DoorkeeperConstants.ADMIN);
        admin.setUsername(StringUtils.isBlank(param.getUsername()) ? DoorkeeperConstants.ADMIN : param.getUsername());
        admin.setPassword(MD5Utils.md5WithSalt(StringUtils.isBlank(param.getPassword()) ? DoorkeeperConstants.ADMIN : param.getPassword()));
        admin.setIsEnabled(true);
        userMapper.insert(admin);
        // 创建doorkeeper超管角色
        RoleAddParam realmAdminRoleParam = createRole(DoorkeeperConstants.REALM_CLIENT_ID, DoorkeeperConstants.ADMIN, "");
        RoleVO realmAdminRole = roleService.add(realm.getRealmId(), realmAdminRoleParam);
        // 绑定管理员和角色
        UserRoleBatchUpdateParam userRoleBatchUpdateParam = new UserRoleBatchUpdateParam();
        userRoleBatchUpdateParam.setType(BatchMethod.SAVE);
        userRoleBatchUpdateParam.setRoleIds(Collections.singletonList(realmAdminRole.getRoleId()));
        userRoleService.batchUpdate(admin.getUserId(), userRoleBatchUpdateParam);
        // 创建api client
        Client apiClient = new Client();
        apiClient.setRealmId(realm.getRealmId());
        apiClient.setName(DoorkeeperConstants.API_CLIENT);
        apiClient.setDescription("manager doorkeeper permission");
        apiClient.setIsEnabled(true);
        clientMapper.insert(apiClient);
        // 资源数据
        createRealmResource(admin.getUserId(), realm.getRealmId(), realm.getName());
        createClientResource(admin.getUserId(), realm.getRealmId(), apiClient.getClientId(), apiClient.getName());
    }

    /**
     * 新建域 在doorkeeper 域下面创建 realmName的client 并创建realm 资源 绑定给用户
     *
     * @param userId    创建者ID
     * @param realmId   新建的RealmId
     * @param realmName 新建的realm名称
     */
    @Transactional
    public void createRealmResource(Long userId, Long realmId, String realmName) {
        Realm doorkeeperRealm = realmMapper.getDoorkeeperRealm();
        Client apiClient = clientMapper.getByName(doorkeeperRealm.getRealmId(), DoorkeeperConstants.API_CLIENT);
        // 域资源
        String resourceTemplate = getRealmResourceTemplate();
        resourceTemplate = resourceTemplate.replaceAll("\\$\\{realmId}", realmId.toString());
        resourceTemplate = resourceTemplate.replaceAll("\\$\\{realmName}", realmName);
        List<ResourceAddParam> resourceAddParams = JacksonUtil.parseObject(resourceTemplate, new TypeReference<List<ResourceAddParam>>() {
        });
        for (ResourceAddParam resourceAddParam : resourceAddParams) {
            resourceService.add(doorkeeperRealm.getRealmId(), apiClient.getClientId(), resourceAddParam);
        }

        User user = userMapper.selectById(userId);
        bindUser(user.getUserId(), user.getUsername(), realmName + "-realm-own", apiClient.getRealmId(), apiClient.getClientId());
    }

    /**
     * 新建client 在doorkeeper 新建client对应 realmName的client下创建对应资源 并绑定给用户
     *
     * @param userId     创建者ID
     * @param realmId    创建client的realmId
     * @param clientId   创建的clientId
     * @param clientName 创建的clientName
     */
    @Transactional
    public void createClientResource(Long userId, Long realmId, Long clientId, String clientName) {
        Realm doorkeeperRealm = realmMapper.getDoorkeeperRealm();
        Client apiClient = clientMapper.getByName(doorkeeperRealm.getRealmId(), DoorkeeperConstants.API_CLIENT);
        Realm realm = realmMapper.selectById(realmId);

        // 资源
        String resourceTemplate = getClientResourceTemplate();
        resourceTemplate = resourceTemplate.replaceAll("\\$\\{realmId}", realmId.toString());
        resourceTemplate = resourceTemplate.replaceAll("\\$\\{realmName}", realm.getName());
        resourceTemplate = resourceTemplate.replaceAll("\\$\\{clientId}", clientId.toString());
        resourceTemplate = resourceTemplate.replaceAll("\\$\\{clientName}", clientName);
        List<ResourceAddParam> resourceAddParams = JacksonUtil.parseObject(resourceTemplate, new TypeReference<List<ResourceAddParam>>() {
        });
        for (ResourceAddParam resourceAddParam : resourceAddParams) {
            resourceService.add(doorkeeperRealm.getRealmId(), apiClient.getClientId(), resourceAddParam);
        }

        User user = userMapper.selectById(userId);
        bindUser(user.getUserId(), user.getUsername(), realm.getName() + "-" + clientName + "-client-own", apiClient.getRealmId(), apiClient.getClientId());
    }

    /**
     * 创建用户策略/权限
     *
     * @param userId   用户ID
     * @param username 用户名称
     * @param name     需要绑定的资源名称
     * @param realmId  doorkeeper realmId
     * @param clientId doorkeeper api clientId
     */
    public void bindUser(Long userId, String username, String name, Long realmId, Long clientId) {
        String uqName = username + "-user";
        // 用户策略
        Wrapper<Policy> policyWrapper = Wrappers.<Policy>lambdaQuery()
                .eq(Policy::getRealmId, realmId)
                .eq(Policy::getClientId, clientId)
                .eq(Policy::getName, uqName);

        Policy policy = policyMapper.selectOne(policyWrapper);
        if (policy == null) {
            try {
                PolicyAddParam policyParam = createUserPolicy(uqName, userId);
                policyService.add(realmId, clientId, policyParam);
            } catch (DuplicateKeyException e) {
                // 重复了，不处理
            }
            policy = policyMapper.selectOne(policyWrapper);
        }

        // 权限
        List<ResourceVO> resources = new ArrayList<>();
        if (StringUtils.isNotBlank(name)) {
            ResourceQueryParam resourceQueryParam = new ResourceQueryParam();
            resourceQueryParam.setRealmId(realmId);
            resourceQueryParam.setClientId(clientId);
            resourceQueryParam.setNames(Collections.singletonList(name));
            resources = resourceMapper.getVOS(resourceQueryParam);
        }
        List<Long> resourceIds = resources.stream().map(ResourceVO::getResourceId).collect(Collectors.toList());

        PermissionQueryParam permissionQueryParam = new PermissionQueryParam();
        permissionQueryParam.setRealmId(realmId);
        permissionQueryParam.setClientId(clientId);
        permissionQueryParam.setNames(Collections.singletonList(uqName));
        List<PermissionVO> vos = permissionMapper.getVOS(permissionQueryParam);
        if (CollectionUtils.isEmpty(vos)) {
            PermissionAddParam realmAdminPermissionParam = createPermission(uqName, resourceIds, policy.getPolicyId());
            permissionService.add(realmId, clientId, realmAdminPermissionParam);
        } else {
            PermissionUpdateParam permissionUpdateParam = new PermissionUpdateParam();
            if (CollectionUtils.isNotEmpty(vos.get(0).getPolicies())) {
                permissionUpdateParam.setPolicyIds(vos.get(0).getPolicies().stream().map(PermissionPolicyVO::getPolicyId).collect(Collectors.toList()));
            }
            if (CollectionUtils.isNotEmpty(vos.get(0).getResources())) {
                resourceIds.addAll(vos.get(0).getResources().stream().map(PermissionResourceVO::getResourceId).collect(Collectors.toList()));
            }
            permissionUpdateParam.setResourceIds(resourceIds);
            permissionService.update(realmId, clientId, vos.get(0).getPermissionId(), permissionUpdateParam);
        }
    }


    private RoleAddParam createRole(Long clientId, String name, String description) {
        RoleAddParam roleAddParam = new RoleAddParam();
        roleAddParam.setName(name);
        roleAddParam.setClientId(clientId);
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

    private PolicyAddParam createUserPolicy(String name, Long userId) {
        PolicyAddParam policyAddParam = new PolicyAddParam();
        policyAddParam.setName(name);
        policyAddParam.setType(PolicyType.USER);
        policyAddParam.setLogic(Logic.ALL);
        policyAddParam.setIntention(Intention.ALLOW);
        policyAddParam.setIsEnabled(Boolean.TRUE);

        PolicyUserAddParam userAddParam = new PolicyUserAddParam();
        userAddParam.setUserId(userId);

        policyAddParam.setUsers(Collections.singletonList(userAddParam));
        return policyAddParam;
    }

    private PermissionAddParam createPermission(String name, List<Long> resourceIds, Long policyId) {
        PermissionAddParam permissionAddParam = new PermissionAddParam();
        permissionAddParam.setName(name);
        permissionAddParam.setLogic(Logic.ALL);
        permissionAddParam.setIntention(Intention.ALLOW);
        permissionAddParam.setIsEnabled(Boolean.TRUE);
        permissionAddParam.setResourceIds(resourceIds);
        permissionAddParam.setPolicyIds(Collections.singletonList(policyId));
        return permissionAddParam;
    }

    /**
     * 获取 realm 资源模板
     */
    private String getRealmResourceTemplate() {
        if (realmResource == null) {
            synchronized (this) {
                if (realmResource == null) {
                    try {
                        File file = ResourceUtils.getFile("classpath:realm_resource.json");
                        realmResource = FileUtils.readFileToString(file, "utf-8");
                    } catch (IOException e) {
                        log.error("read realm_resource.json error", e);
                    }
                }
            }
        }
        return realmResource;
    }

    /**
     * 获取 client 资源模板
     */
    private String getClientResourceTemplate() {
        if (clientResource == null) {
            synchronized (this) {
                if (clientResource == null) {
                    try {
                        File file = ResourceUtils.getFile("classpath:client_resource.json");
                        clientResource = FileUtils.readFileToString(file, "utf-8");
                    } catch (IOException e) {
                        log.error("read client_resource.json error", e);
                    }
                }
            }
        }
        return clientResource;
    }

    /**
     * 判断用户是否doorkeeper的管理员 校验是否为 doorkeeper域下的域管理员
     *
     * @param userId 用户ID
     * @return 是否超级管理员
     */
    public boolean isSuperAdmin(Long userId) {
        Realm doorkeeperRealm = realmMapper.getDoorkeeperRealm();
        User user = userMapper.selectById(userId);
        Role doorkeeperAdmin = roleMapper.getByName(doorkeeperRealm.getRealmId(), DoorkeeperConstants.REALM_CLIENT_ID, DoorkeeperConstants.ADMIN);
        PolicyRoleVO policyRoleVO = new PolicyRoleVO();
        policyRoleVO.setRoleId(doorkeeperAdmin.getRoleId());
        policyRoleVO.setIsEnabled(doorkeeperAdmin.getIsEnabled());
        PolicyVO adminPolicy = new PolicyVO();
        adminPolicy.setRealmId(doorkeeperRealm.getRealmId());
        adminPolicy.setLogic(Logic.ALL.getValue());
        adminPolicy.setType(PolicyType.ROLE.getValue());
        adminPolicy.setIntention(Intention.ALLOW.getValue());
        adminPolicy.setRoles(Collections.singletonList(policyRoleVO));
        Intention policyCheckIntention = policyCheckerFactory.newPolicyChecker(user, adminPolicy).check(new PolicyCheckerParam());
        return Intention.ALLOW == policyCheckIntention;
    }

}
