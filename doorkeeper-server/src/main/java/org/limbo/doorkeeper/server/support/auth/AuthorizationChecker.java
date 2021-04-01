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

package org.limbo.doorkeeper.server.support.auth;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.api.model.param.check.AuthorizationCheckParam;
import org.limbo.doorkeeper.api.model.param.check.UriCheckParam;
import org.limbo.doorkeeper.api.model.param.resource.ResourceQueryParam;
import org.limbo.doorkeeper.api.model.vo.PermissionPolicyVO;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.limbo.doorkeeper.api.model.vo.ResourceVO;
import org.limbo.doorkeeper.api.model.vo.check.AuthorizationCheckResult;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.dal.dao.PermissionDao;
import org.limbo.doorkeeper.server.dal.dao.PolicyDao;
import org.limbo.doorkeeper.server.dal.entity.Client;
import org.limbo.doorkeeper.server.dal.entity.PermissionResource;
import org.limbo.doorkeeper.server.dal.entity.ResourceUri;
import org.limbo.doorkeeper.server.dal.mapper.ClientMapper;
import org.limbo.doorkeeper.server.dal.mapper.PermissionResourceMapper;
import org.limbo.doorkeeper.server.dal.mapper.ResourceMapper;
import org.limbo.doorkeeper.server.dal.mapper.ResourceUriMapper;
import org.limbo.doorkeeper.server.support.auth.policies.PolicyCheckerFactory;
import org.limbo.doorkeeper.server.utils.EasyAntPathMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 授权校验器
 *
 * @author Devil
 * @date 2021/3/31 2:04 下午
 */
@Slf4j
@Component
public class AuthorizationChecker {

    @Autowired
    private PermissionDao permissionDao;

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

    /**
     * 策略检查器工厂
     */
    @Autowired
    private PolicyCheckerFactory policyCheckerFactory;

    /**
     * 未授权情况下是否拒绝，默认ture。
     * 未授权是指，资源找不到对应的Permission
     */
    @Setter
    private boolean refuseWhenUnauthorized = true;

    private static final ThreadLocal<EasyAntPathMatcher> PATH_MATCHER = ThreadLocal.withInitial(EasyAntPathMatcher::new);

    /**
     * 校验参数
     */
    protected AuthorizationCheckParam checkParam;

    /**
     * 校验参数中clientId对应的client
     */
    protected Client client;

    /**
     * 进行权限校验，是否有资格访问
     *
     * @return 校验结果，分为refused和allowed两类
     */
    public AuthorizationCheckResult check(AuthorizationCheckParam checkParam) {
        this.checkParam = checkParam;
        try {
            List<ResourceVO> result = new ArrayList<>();

            // 找到待检测的启用资源
            List<ResourceVO> findResources = assignCheckingResources();
            ASSIGNER_ITER:
            for (ResourceVO findResource : findResources) {
                // 遍历资源依次拿到权限Permission
                List<PermissionVO> permissions = findResourcePermissions(findResource.getResourceId());

                // 未授权的情况
                if (CollectionUtils.isEmpty(permissions)) {
                    if (refuseWhenUnauthorized) {
                        continue;
                    } else {
                        result.add(findResource);
                    }
                }

                // 对Permission的Intention进行分组
                Map<Intention, Set<PermissionVO>> intentGroupedPerms = permissions.stream().collect(Collectors.groupingBy(
                        permissionVO -> Intention.parse(permissionVO.getIntention()),
                        Collectors.mapping(Function.identity(), Collectors.toSet())
                ));
                // 先检测 REFUSE 的权限，如果存在一个 REFUSE 的权限校验通过，则此资源约束被看作拒绝
                Set<PermissionVO> refusedPerms = intentGroupedPerms.getOrDefault(Intention.REFUSE, new HashSet<>());
                for (PermissionVO permission : refusedPerms) {
                    if (checkPermissionLogic(permission)) {
                        continue ASSIGNER_ITER;
                    }
                }
                // 再检测 ALLOW 的权限
                Set<PermissionVO> allowedPerms = intentGroupedPerms.getOrDefault(Intention.ALLOW, new HashSet<>());
                for (PermissionVO permission : allowedPerms) {
                    if (checkPermissionLogic(permission)) {
                        result.add(findResource);
                        continue ASSIGNER_ITER;
                    }
                }
            }

            return new AuthorizationCheckResult(result);

        } catch (Exception e) {
            log.error("鉴权校验失败", e);
            throw new AuthorizationException(e.getMessage());
        }
    }

    /**
     * 异步初始化 根据校验参数，查询委托方
     *
     * @return 委托方PO
     * @throws IllegalArgumentException 当根据clientId查询不到委托方时，会抛出异常
     */
    protected Client getClient() throws IllegalArgumentException {
        if (this.client == null) {
            synchronized (this) {
                if (this.client == null) {
                    this.client = clientMapper.selectById(checkParam.getClientId());
                }
            }

            if (client == null) {
                throw new AuthorizationException("无法找到Client，clientId=" + checkParam.getClientId());
            }
            if (!client.getIsEnabled()) {
                throw new AuthorizationException("此Client未启用");
            }
        }

        return this.client;
    }

    /**
     * 决定资源约束对应着哪些资源。
     *
     * @return 返回资源列表
     */
    protected List<ResourceVO> assignCheckingResources() {
        // 处理标签属性
        List<String> kvs = null;
        if (MapUtils.isNotEmpty(checkParam.getTags())) {
            kvs = new ArrayList<>();
            for (Map.Entry<String, String> entry : checkParam.getTags().entrySet()) {
                kvs.add(entry.getKey() + DoorkeeperConstants.KV_DELIMITER + entry.getValue());
            }
        }
        // 获取uri资源id
        List<Long> resourceIds = null;
        if (CollectionUtils.isNotEmpty(checkParam.getUris())) {
            // client拥有的全部uri资源
            List<ResourceUri> clientUris = resourceUriMapper.selectList(Wrappers.<ResourceUri>lambdaQuery()
                    .eq(ResourceUri::getRealmId, getClient().getRealmId())
                    .eq(ResourceUri::getClientId, getClient().getClientId())
            );

            resourceIds = clientUris.stream()
                    .filter(resourceUri -> {
                        for (UriCheckParam uriCheckParam : checkParam.getUris()) {
                            if (resourceUri.getMethod() != uriCheckParam.getMethod()) {
                                return false;
                            }
                            if (pathMatch(resourceUri.getUri(), uriCheckParam.getUri())) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .map(ResourceUri::getResourceId)
                    .collect(Collectors.toList());
        }
        ResourceQueryParam param = new ResourceQueryParam();
        param.setRealmId(getClient().getRealmId());
        param.setClientId(getClient().getClientId());
        param.setResourceIds(resourceIds);
        param.setNames(checkParam.getNames());
        param.setKvs(kvs);
        param.setIsEnabled(true);
        param.setNeedAll(true);
        return resourceMapper.getVOS(param);
    }


    /**
     * 找到资源关联的权限
     *
     * @return 资源关联的权限列表
     */
    protected List<PermissionVO> findResourcePermissions(Long resourceId) {
        List<PermissionResource> permissionResources = permissionResourceMapper.selectList(Wrappers.<PermissionResource>lambdaQuery()
                .eq(PermissionResource::getResourceId, resourceId)
        );
        if (CollectionUtils.isEmpty(permissionResources)) {
            return new ArrayList<>();
        }
        List<Long> permissionIds = permissionResources.stream().map(PermissionResource::getPermissionId).collect(Collectors.toList());
        return permissionDao.getVOSByPermissionIds(getClient().getRealmId(), getClient().getClientId(), permissionIds, true);
    }


    /**
     * 进行Permission的校验
     *
     * @param permission 待校验的授权信息
     * @return 返回Permission校验是否通过
     */
    protected boolean checkPermissionLogic(PermissionVO permission) {
        // 检测权限是否禁用
        if (!permission.getIsEnabled()) {
            return false;
        }

        Logic logic = Logic.parse(permission.getLogic());
        if (logic == null) {
            throw new IllegalArgumentException("无法解析权限的策略，permission=" + permission);
        }

        List<PermissionPolicyVO> permissionPolicies = permission.getPolicies();
        if (CollectionUtils.isEmpty(permissionPolicies)) {
            return false;
        }

        // 逐个policy检查
        List<Long> policyIds = permissionPolicies.stream().map(PermissionPolicyVO::getPolicyId).collect(Collectors.toList());
        List<PolicyVO> policies = policyDao.getVOSByPolicyIds(getClient().getRealmId(), getClient().getClientId(), policyIds, true);
        if (CollectionUtils.isEmpty(policies)) {
            return false;
        }

        int allowedCount = 0;
        for (PolicyVO policy : policies) {
            Intention policyCheckIntention = policyCheckerFactory.newPolicyChecker(policy).check(checkParam);
            // 统计允许的policy个数
            if (Intention.ALLOW == policyCheckIntention) {
                allowedCount++;
            }
        }

        return LogicChecker.isSatisfied(logic, policies.size(), allowedCount);
    }

    /**
     * 判断path是否符合ant风格的pattern
     *
     * @param pattern ant风格的路径pattern
     * @param path    访问的路径
     * @return 是否匹配
     */
    public boolean pathMatch(String pattern, String path) {
        EasyAntPathMatcher antPathMatcher = PATH_MATCHER.get();
        return antPathMatcher.match(StringUtils.trim(pattern), StringUtils.trim(path));
    }
}
