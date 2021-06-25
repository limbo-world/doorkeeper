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

package org.limbo.doorkeeper.server.infrastructure.checker;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.api.constants.UriMethod;
import org.limbo.doorkeeper.api.model.param.query.PermissionQueryParam;
import org.limbo.doorkeeper.api.model.param.query.PolicyCheckerParam;
import org.limbo.doorkeeper.api.model.param.query.ResourceCheckParam;
import org.limbo.doorkeeper.api.model.param.query.ResourceQueryParam;
import org.limbo.doorkeeper.api.model.vo.PermissionPolicyVO;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.limbo.doorkeeper.api.model.vo.ResourceVO;
import org.limbo.doorkeeper.api.model.vo.check.ResourceCheckResult;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.domain.PatternDO;
import org.limbo.doorkeeper.server.infrastructure.dao.PolicyDao;
import org.limbo.doorkeeper.server.infrastructure.exception.AuthorizationException;
import org.limbo.doorkeeper.server.infrastructure.mapper.*;
import org.limbo.doorkeeper.server.infrastructure.po.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 资源校验器
 *
 * @author Devil
 * @date 2021/3/31 2:04 下午
 */
@Slf4j
@Component
public class ResourceChecker {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private PolicyDao policyDao;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private PermissionResourceMapper permissionResourceMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private ResourceUriMapper resourceUriMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UriMapper uriMapper;

    /**
     * 策略检查器工厂
     */
    @Autowired
    private PolicyCheckerFactory policyCheckerFactory;

    /**
     * 未授权情况下是否拒绝，未授权是指，资源找不到对应的Permission
     */
    private boolean refuseWhenUnauthorized = true;

    /**
     * 进行权限校验，是否有资格访问
     *
     * @param userId     用户id
     * @param checkParam 用于获取资源的参数
     * @return
     */
    public ResourceCheckResult check(Long userId, ResourceCheckParam checkParam) {
        ClientPO client = getClient(checkParam.getClientId());

        UserPO user = getUser(userId);
        if (!user.getIsEnabled()) {
            return emptyResult();
        }

        try {
            // 找到待检测的启用资源
            List<ResourceVO> resources = findResources(client.getRealmId(), client.getClientId(), checkParam);
            if (CollectionUtils.isEmpty(resources)) {
                return emptyResult();
            }

            // 找到资源权限关系
            List<PermissionResourcePO> permissionResources = permissionResourceMapper.selectList(Wrappers.<PermissionResourcePO>lambdaQuery()
                    .in(PermissionResourcePO::getResourceId, resources.stream().map(ResourceVO::getResourceId).collect(Collectors.toList()))
            );
            if (CollectionUtils.isEmpty(permissionResources)) {
                return checkResourceRefuseResult(resources);
            }
            Set<Long> permissionIds = new HashSet<>();
            Map<Long, List<Long>> resourcePermissionMap = new HashMap<>();
            for (PermissionResourcePO permissionResource : permissionResources) {
                permissionIds.add(permissionResource.getPermissionId());
                if (!resourcePermissionMap.containsKey(permissionResource.getResourceId())) {
                    resourcePermissionMap.put(permissionResource.getResourceId(), new ArrayList<>());
                }
                resourcePermissionMap.get(permissionResource.getResourceId()).add(permissionResource.getPermissionId());
            }

            // 查询权限
            List<PermissionVO> allPermissions = getPermissions(client.getRealmId(), client.getClientId(), new ArrayList<>(permissionIds));
            if (CollectionUtils.isEmpty(allPermissions)) {
                return checkResourceRefuseResult(resources);
            }

            // 获取策略ID
            Map<Long, PermissionVO> permissionMap = new HashMap<>();
            Set<Long> policyIds = new HashSet<>();
            for (PermissionVO permission : allPermissions) {
                if (Logic.parse(permission.getLogic()) == null) {
                    throw new IllegalArgumentException("无法解析权限的策略，permission=" + permission);
                }
                permissionMap.put(permission.getPermissionId(), permission);
                if (CollectionUtils.isNotEmpty(permission.getPolicies())) {
                    policyIds.addAll(permission.getPolicies().stream().map(PermissionPolicyVO::getPolicyId).collect(Collectors.toList()));
                }
            }
            if (CollectionUtils.isEmpty(policyIds)) {
                return checkResourceRefuseResult(resources);
            }

            // 获取策略
            List<PolicyVO> allPolicies = policyDao.getVOSByPolicyIds(client.getRealmId(), client.getClientId(), new ArrayList<>(policyIds), true);
            if (CollectionUtils.isEmpty(allPolicies)) {
                return checkResourceRefuseResult(resources);
            }
            Map<Long, PolicyVO> policyMap = allPolicies.stream().collect(Collectors.toMap(PolicyVO::getPolicyId, policyVO -> policyVO));

            // 获取策略校验器
            PolicyChecker checker = policyCheckerFactory.newPolicyChecker(user);

            List<ResourceVO> result = new ArrayList<>();
            ASSIGNER_ITER:
            for (ResourceVO resource : resources) {
                // 获取资源权限ID
                List<Long> resourcePermissionIds = resourcePermissionMap.get(resource.getResourceId());
                if (CollectionUtils.isEmpty(resourcePermissionIds)) {
                    if (refuseWhenUnauthorized) {
                        continue;
                    } else {
                        result.add(resource);
                    }
                }

                // 获取资源权限
                List<PermissionVO> permissionVOS = new ArrayList<>();
                for (Long permissionId : resourcePermissionIds) {
                    if (permissionMap.containsKey(permissionId)) {
                        permissionVOS.add(permissionMap.get(permissionId));
                    }
                }
                if (CollectionUtils.isEmpty(permissionVOS)) {
                    if (refuseWhenUnauthorized) {
                        continue;
                    } else {
                        result.add(resource);
                    }
                }

                // 对Permission的Intention进行分组
                Map<Intention, Set<PermissionVO>> intentGroupedPerms = permissionVOS.stream().collect(Collectors.groupingBy(
                        permissionVO -> Intention.parse(permissionVO.getIntention()),
                        Collectors.mapping(Function.identity(), Collectors.toSet())
                ));

                // 先检测 REFUSE 的权限，如果存在一个 REFUSE 的权限校验通过，则此资源约束被看作拒绝
                Set<PermissionVO> refusedPerms = intentGroupedPerms.getOrDefault(Intention.REFUSE, new HashSet<>());
                for (PermissionVO permission : refusedPerms) {
                    if (checkPermissionLogic(checker, checkParam, permission, policyMap)) {
                        continue ASSIGNER_ITER;
                    }
                }
                // 再检测 ALLOW 的权限
                Set<PermissionVO> allowedPerms = intentGroupedPerms.getOrDefault(Intention.ALLOW, new HashSet<>());
                for (PermissionVO permission : allowedPerms) {
                    if (checkPermissionLogic(checker, checkParam, permission, policyMap)) {
                        result.add(resource);
                        continue ASSIGNER_ITER;
                    }
                }

            }
            return new ResourceCheckResult(result);

        } catch (Exception e) {
            log.error("鉴权校验失败", e);
            throw new AuthorizationException(e.getMessage());
        }
    }

    /**
     * 根据参数获取需要校验的资源
     *
     * @return 返回资源列表
     */
    private List<ResourceVO> findResources(Long realmId, Long clientId, ResourceCheckParam checkParam) {
        List<Long> resourceIds = checkParam.getResourceIds();
        if (CollectionUtils.isEmpty(resourceIds)) {
            resourceIds = new ArrayList<>();
        }
        // 获取uri资源id
        if (CollectionUtils.isNotEmpty(checkParam.getUris())) {
            List<Long> uriIds = new ArrayList<>();

            // client拥有的全部uri资源
            List<UriPO> clientUris = uriMapper.selectList(Wrappers.<UriPO>lambdaQuery()
                    .eq(UriPO::getRealmId, realmId)
                    .eq(UriPO::getClientId, clientId)
            );

            // 根据路径和请求方式，获取资源ID
            // 对于所有的uri 如果匹配 checkParam 其中的某一项 则表示对应资源需要返回
            for (UriPO uri : clientUris) {
                for (String str : checkParam.getUris()) {
                    String requestMethod = UriMethod.ALL.getValue();
                    String requestUri;
                    if (str.contains(DoorkeeperConstants.KV_DELIMITER)) {
                        String[] split = str.split(DoorkeeperConstants.KV_DELIMITER);
                        requestMethod = split[0];
                        requestUri = split[1];
                    } else {
                        requestUri = str;
                    }

                    // 判断是否匹配方法和路径
                    PatternDO pattern = new PatternDO(uri.getUri().trim());
                    if ((UriMethod.ALL == uri.getMethod() || uri.getMethod() == UriMethod.parse(requestMethod))
                            && pattern.pathMatch(requestUri.trim())) {
                        uriIds.add(uri.getUriId());
                        break;
                    }
                }
            }

            // 如果匹配到的资源为空 则返回空
            if (CollectionUtils.isEmpty(uriIds)) {
                return new ArrayList<>();
            }

            List<ResourceUriPO> resourceUris = resourceUriMapper.selectList(Wrappers.<ResourceUriPO>lambdaQuery()
                    .in(ResourceUriPO::getUriId, uriIds)
            );
            resourceIds.addAll(resourceUris.stream().map(ResourceUriPO::getResourceId).collect(Collectors.toList()));
            // 如果匹配到的资源为空 则返回空
            if (CollectionUtils.isEmpty(resourceIds)) {
                return new ArrayList<>();
            }

        }

        ResourceQueryParam param = new ResourceQueryParam();
        param.setRealmId(realmId);
        param.setClientId(clientId);
        param.setResourceIds(resourceIds);
        param.setNames(checkParam.getNames());
        param.setAndKvs(checkParam.getAndTags());
        param.setOrKvs(checkParam.getOrTags());
        param.setIsEnabled(true);
        param.setNeedAll(true);
        param.setNeedTag(checkParam.getNeedTag());
        param.setNeedUri(checkParam.getNeedUri());
        param.setNeedParentId(checkParam.getNeedParentId());
        param.setNeedChildrenId(checkParam.getNeedChildrenId());
        return resourceMapper.getVOS(param);
    }


    /**
     * 找到资源关联的权限
     *
     * @return 资源关联的权限列表
     */
    private List<PermissionVO> findResourcePermissions(Long realmId, Long clientId, Long resourceId) {
        List<PermissionResourcePO> permissionResources = permissionResourceMapper.selectList(Wrappers.<PermissionResourcePO>lambdaQuery()
                .eq(PermissionResourcePO::getResourceId, resourceId)
        );
        if (CollectionUtils.isEmpty(permissionResources)) {
            return new ArrayList<>();
        }
        List<Long> permissionIds = permissionResources.stream().map(PermissionResourcePO::getPermissionId).collect(Collectors.toList());
        PermissionQueryParam param = new PermissionQueryParam();
        param.setRealmId(realmId);
        param.setClientId(clientId);
        param.setPermissionIds(permissionIds);
        param.setIsEnabled(true);
        param.setNeedAll(true);
        return permissionMapper.getVOS(param);
    }


    /**
     * 进行Permission的校验
     *
     * @param permission 待校验的授权信息
     * @return 返回Permission校验是否通过
     */
    private boolean checkPermissionLogic(PolicyChecker checker, ResourceCheckParam checkParam, PermissionVO permission,
                                         Map<Long, PolicyVO> policyMap) {
        // 检测权限是否禁用
        if (!permission.getIsEnabled()) {
            return false;
        }

        Logic logic = Logic.parse(permission.getLogic());
        if (logic == null) {
            throw new IllegalArgumentException("无法解析权限的策略，permission=" + permission);
        }

        if (CollectionUtils.isEmpty(permission.getPolicies())) {
            return false;
        }

        // 逐个policy检查
        int allowedCount = 0;
        int totalCount = 0;
        for (PermissionPolicyVO permissionPolicy : permission.getPolicies()) {
            PolicyVO policy = policyMap.get(permissionPolicy.getPolicyId());
            if (policy == null) {
                continue;
            }
            // todo 判断是否能进行校验
//            PolicyChecker policyChecker = policyCheckerFactory.newPolicyChecker(user, policy);
//            if (policyChecker == null) {
//                continue;
//            }
            PolicyCheckerParam policyCheckerParam = new PolicyCheckerParam();
            policyCheckerParam.setParams(checkParam.getParams());
            totalCount++;
            // 统计允许的policy个数
            if (checker.check(policy, policyCheckerParam.getParams()).allow()) {
                allowedCount++;
            }

        }

        return Logic.isSatisfied(logic, totalCount, allowedCount);
    }

    /**
     * 获取client
     *
     * @param clientId
     * @return
     */
    private ClientPO getClient(Long clientId) {
        ClientPO client = clientMapper.selectById(clientId);
        if (client == null) {
            throw new AuthorizationException("无法找到Client，clientId=" + clientId);
        }
        if (!client.getIsEnabled()) {
            throw new AuthorizationException("此Client未启用");
        }
        return client;
    }

    private UserPO getUser(Long userId) {
        UserPO user = userMapper.selectById(userId);
        if (user == null) {
            throw new AuthorizationException("无法找到用户，Id=" + userId);
        }
        return user;
    }

    /**
     * 根据决策判断在未授权情况返回所有资源还是空
     *
     * @param resources
     * @return
     */
    private ResourceCheckResult checkResourceRefuseResult(List<ResourceVO> resources) {
        if (!refuseWhenUnauthorized) {
            return new ResourceCheckResult(resources);
        }
        return new ResourceCheckResult(new ArrayList<>());
    }

    /**
     * 返回空结果
     *
     * @return
     */
    private ResourceCheckResult emptyResult() {
        return new ResourceCheckResult(new ArrayList<>());
    }

    private List<PermissionVO> getPermissions(Long realmId, Long clientId, List<Long> permissionIds) {
        PermissionQueryParam param = new PermissionQueryParam();
        param.setRealmId(realmId);
        param.setClientId(clientId);
        param.setPermissionIds(permissionIds);
        param.setIsEnabled(true);
        param.setNeedAll(true);
        return permissionMapper.getVOS(param);
    }
}
