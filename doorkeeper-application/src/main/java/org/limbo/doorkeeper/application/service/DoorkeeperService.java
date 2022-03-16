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

package org.limbo.doorkeeper.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.constants.*;
import org.limbo.doorkeeper.api.dto.param.add.*;
import org.limbo.doorkeeper.api.dto.param.query.PermissionQueryParam;
import org.limbo.doorkeeper.api.dto.param.query.ResourceCheckParam;
import org.limbo.doorkeeper.api.dto.param.query.ResourceQueryParam;
import org.limbo.doorkeeper.api.dto.param.update.PermissionUpdateParam;
import org.limbo.doorkeeper.api.dto.param.update.RealmUpdateParam;
import org.limbo.doorkeeper.api.dto.vo.*;
import org.limbo.doorkeeper.api.dto.vo.check.ResourceCheckResult;
import org.limbo.doorkeeper.core.component.checker.ResourceChecker;
import org.limbo.doorkeeper.core.component.checker.RoleChecker;
import org.limbo.doorkeeper.core.domian.aggregate.User;
import org.limbo.doorkeeper.infrastructure.config.TemplateLoader;
import org.limbo.doorkeeper.infrastructure.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.infrastructure.dao.RealmDao;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.*;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.policy.PolicyMapper;
import org.limbo.doorkeeper.infrastructure.dto.RealmDTO;
import org.limbo.doorkeeper.infrastructure.po.NamespacePO;
import org.limbo.doorkeeper.infrastructure.po.PolicyPO;
import org.limbo.doorkeeper.infrastructure.po.RealmPO;
import org.limbo.doorkeeper.infrastructure.po.UserPO;
import org.limbo.utils.jackson.JacksonUtils;
import org.limbo.utils.reflection.EnhancedBeanUtils;
import org.limbo.utils.strings.UUIDUtils;
import org.limbo.utils.verifies.Verifies;
import org.limbo.utils.verifies.VerifyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * doorkeeper资源等管理逻辑
 *
 * @author Devil
 * @since 2021/1/10 10:39 上午
 */
@Slf4j
@Service
public class DoorkeeperService {

    @Resource
    private RealmDao realmDao;

    @Autowired
    private RealmMapper realmMapper;

    @Autowired
    private NamespaceMapper namespaceMapper;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private ResourceChecker resourceChecker;

    @Autowired
    private RoleChecker roleChecker;

    private final TemplateLoader realmResourceLoader = new TemplateLoader("realm_resource.json");

    private final TemplateLoader clientResourceLoader = new TemplateLoader("client_resource.json");

    /**
     * 系统数据初始化
     *
     * @return 是否完成初始化进程 false表示以前已经进行过初始化
     */
    @Transactional
    public boolean init() {
        String realmName = DoorkeeperConstants.DOORKEEPER_REALM_NAME;
        Long realmId = realmDao.add(DoorkeeperConstants.DOORKEEPER_REALM_NAME, UUIDUtils.randomID());

        // 创建管理员账户
        User admin = User.builder()
                .realmId(realmId)
                .nickname(DoorkeeperConstants.ADMIN)
                .username(DoorkeeperConstants.ADMIN)
                .password(DoorkeeperConstants.ADMIN)
                .isEnabled(true)
                .build();
        Long userId = userRepository.save(admin);

        // todo 直接创建 realm 资源 使用用户策略绑定资源
        // 创建 doorkeeper api namespace
        NamespacePO apiClient = new NamespacePO();
        apiClient.setRealmId(realmId);
        apiClient.setName(DoorkeeperConstants.API_CLIENT);
        apiClient.setDescription("manager doorkeeper permission");
        apiClient.setIsEnabled(true);
        namespaceMapper.insert(apiClient);
        // 资源数据
        createRealmResource(admin.getUserId(), realmId, realmName);
        createClientResource(admin.getUserId(), realmId, apiClient.getNamespaceId(), apiClient.getName());
        return true;
    }

    @Transactional
    public Long addRealm(Long userId, RealmAddParam param) {
        if (StringUtils.isBlank(param.getSecret())) {
            param.setSecret(UUIDUtils.randomID());
        }
        try {
            Long realmId = realmDao.add(param.getName(), param.getSecret());
            // 初始化realm数据
            createRealmResource(userId, realmId, param.getName());

            return realmId;
        } catch (DuplicateKeyException e) {
            throw new VerifyException("域已存在");
        }
    }

    public RealmVO get(Long realmId) {
        RealmPO realm = realmDao.getById(realmId);
        return RealmVO.builder()
                .realmId(realm.getRealmId())
                .name(realm.getName())
                .secret(realm.getSecret())
                .build();
    }

    public void update(Long realmId, RealmUpdateParam param) {
        realmDao.updateById(realmId, param.getName(), param.getSecret());
    }

    /**
     * user拥有哪些realm
     */
    public List<RealmVO> userRealms(Long userId) {
        NamespacePO apiClient = namespaceMapper.getByName(getDoorkeeperRealmId(), DoorkeeperConstants.API_CLIENT);
        // 普通用户，查看绑定的realm 资源
        ResourceCheckParam checkParam = new ResourceCheckParam();
        checkParam.setClientId(apiClient.getNamespaceId());
        checkParam.setOrTags(Collections.singletonList("type=realmOwn"));
        checkParam.setNeedTag(true);
        ResourceCheckResult check = resourceChecker.check(userId, checkParam);
        if (CollectionUtils.isEmpty(check.getResources())) {
            return new ArrayList<>();
        }

        List<Long> realmIds = new ArrayList<>();
        for (ResourceVO resource : check.getResources()) {
            if (CollectionUtils.isEmpty(resource.getTags())) {
                continue;
            }
            for (ResourceTagVO tag : resource.getTags()) {
                if (DoorkeeperConstants.REALM_ID.equals(tag.getK())) {
                    realmIds.add(Long.valueOf(tag.getV()));
                    break;
                }
            }
        }

        List<RealmPO> realms = realmMapper.selectList(Wrappers.<RealmPO>lambdaQuery().select(RealmPO::getRealmId, RealmPO::getName)
                .in(RealmPO::getRealmId, realmIds)
        );
        return EnhancedBeanUtils.createAndCopyList(realms, RealmVO.class);
    }

    @Transactional
    public NamespaceVO addNamespace(Long realmId, Long userId, NamespaceAddParam param) {
        NamespacePO namespace = EnhancedBeanUtils.createAndCopy(param, NamespacePO.class);
        namespace.setRealmId(realmId);
        try {
            namespaceMapper.insert(namespace);
        } catch (DuplicateKeyException e) {
            throw new VerifyException("委托方已存在");
        }

        // 初始化client数据
        createClientResource(userId, realmId, namespace.getNamespaceId(), namespace.getName());

        return EnhancedBeanUtils.createAndCopy(namespace, NamespaceVO.class);
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
        NamespacePO apiClient = namespaceMapper.getByName(getDoorkeeperRealmId(), DoorkeeperConstants.API_CLIENT);
        // 域资源
        String resourceTemplate = realmResourceLoader.getTemplate();
        resourceTemplate = resourceTemplate.replaceAll("\\$\\{realmId}", realmId.toString());
        resourceTemplate = resourceTemplate.replaceAll("\\$\\{realmName}", realmName);
        List<ResourceAddParam> resourceAddParams = JacksonUtils.parseObject(resourceTemplate, new TypeReference<List<ResourceAddParam>>() {
        });
        for (ResourceAddParam resourceAddParam : resourceAddParams) {
            resourceService.add(getDoorkeeperRealmId(), apiClient.getNamespaceId(), resourceAddParam);
        }

        UserPO user = userMapper.selectById(userId);
        List<String> resourceNames = new ArrayList<>();
        resourceNames.add(realmName + "-realm-join");
        resourceNames.add(realmName + "-realm-manager");
        bindUser(user.getUserId(), user.getUsername(), resourceNames, apiClient.getRealmId(), apiClient.getNamespaceId());
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
        NamespacePO apiClient = namespaceMapper.getByName(getDoorkeeperRealmId(), DoorkeeperConstants.API_CLIENT);
        RealmPO realm = realmMapper.selectById(realmId);

        // 资源
        String resourceTemplate = clientResourceLoader.getTemplate();
        resourceTemplate = resourceTemplate.replaceAll("\\$\\{realmId}", realmId.toString());
        resourceTemplate = resourceTemplate.replaceAll("\\$\\{realmName}", realm.getName());
        resourceTemplate = resourceTemplate.replaceAll("\\$\\{clientId}", clientId.toString());
        resourceTemplate = resourceTemplate.replaceAll("\\$\\{clientName}", clientName);
        List<ResourceAddParam> resourceAddParams = JacksonUtils.parseObject(resourceTemplate, new TypeReference<List<ResourceAddParam>>() {
        });
        for (ResourceAddParam resourceAddParam : resourceAddParams) {
            resourceService.add(getDoorkeeperRealmId(), apiClient.getNamespaceId(), resourceAddParam);
        }

        UserPO user = userMapper.selectById(userId);
        List<String> resourceNames = new ArrayList<>();
        resourceNames.add(realm.getName() + "-" + clientName + "-client-join");
        resourceNames.add(realm.getName() + "-" + clientName + "-client-manager");
        bindUser(user.getUserId(), user.getUsername(), resourceNames, apiClient.getRealmId(), apiClient.getNamespaceId());
    }

    /**
     * 创建用户策略/权限
     *
     * @param userId        用户ID
     * @param username      用户名称
     * @param resourceNames 需要绑定的资源名称
     * @param realmId       doorkeeper realmId
     * @param clientId      doorkeeper api clientId
     */
    public void bindUser(Long userId, String username, List<String> resourceNames, Long realmId, Long clientId) {
        String uqName = username + "-user";
        // 用户策略
        Wrapper<PolicyPO> policyWrapper = Wrappers.<PolicyPO>lambdaQuery()
                .eq(PolicyPO::getRealmId, realmId)
                .eq(PolicyPO::getClientId, clientId)
                .eq(PolicyPO::getName, uqName);

        PolicyPO policy = policyMapper.selectOne(policyWrapper);
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
        if (CollectionUtils.isNotEmpty(resourceNames)) {
            ResourceQueryParam resourceQueryParam = new ResourceQueryParam();
            resourceQueryParam.setRealmId(realmId);
            resourceQueryParam.setClientId(clientId);
            resourceQueryParam.setNames(resourceNames);
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
     * 是否有路径访问权限
     */
    public boolean hasUriPermission(UserPO user, String path, UriMethod method) {
        // 判断用户是否属于doorkeeper域或公有域
        if (!getDoorkeeperRealmId().equals(user.getRealmId())) {
            return false;
        }

        // 判断uri权限
        RealmPO doorkeeperRealm = getDoorkeeperRealm();
        NamespacePO apiClient = namespaceMapper.getByName(doorkeeperRealm.getRealmId(), DoorkeeperConstants.API_CLIENT);
        ResourceCheckParam checkParam = ResourceCheckParam.builder()
                .clientId(apiClient.getNamespaceId())
                .uris(Collections.singletonList(method + ApiConstants.KV_DELIMITER + path)).build();
        ResourceCheckResult checkResult = resourceChecker.check(user.getUserId(), checkParam);

        return checkResult.getResources().size() > 0;
    }

    public Long getDoorkeeperRealmId() {
        return getDoorkeeperRealm().getRealmId();
    }

    public RealmPO getDoorkeeperRealm() {
        RealmPO doorkeeperRealm = realmMapper.selectOne(Wrappers.<RealmPO>lambdaQuery()
                .eq(RealmPO::getName, DoorkeeperConstants.DOORKEEPER_REALM_NAME)
        );
        Verifies.notNull(doorkeeperRealm, "doorkeeper域不存在");
        return doorkeeperRealm;
    }

}
