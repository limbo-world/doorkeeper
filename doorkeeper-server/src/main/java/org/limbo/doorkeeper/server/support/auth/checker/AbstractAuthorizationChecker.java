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

package org.limbo.doorkeeper.server.support.auth.checker;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.api.model.vo.AuthorizationCheckResult;
import org.limbo.doorkeeper.api.model.vo.PermissionPolicyVO;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.dao.ClientMapper;
import org.limbo.doorkeeper.server.dao.PermissionResourceMapper;
import org.limbo.doorkeeper.server.entity.Client;
import org.limbo.doorkeeper.server.entity.PermissionResource;
import org.limbo.doorkeeper.server.entity.Resource;
import org.limbo.doorkeeper.server.service.PermissionService;
import org.limbo.doorkeeper.server.service.policy.PolicyService;
import org.limbo.doorkeeper.api.model.param.auth.AuthorizationCheckParam;
import org.limbo.doorkeeper.server.support.auth.AuthorizationException;
import org.limbo.doorkeeper.server.support.auth.policies.PolicyCheckerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 目前非线程安全，每次校验需要生成一个新的
 *
 * @author brozen
 * @date 2021/1/14
 */
public abstract class AbstractAuthorizationChecker<P extends AuthorizationCheckParam<T>, T> implements AuthorizationChecker<P, T> {

    @Setter
    private PermissionService permissionService;

    @Setter
    private PolicyService policyService;

    @Setter
    private ClientMapper clientMapper;

    @Setter
    private PermissionResourceMapper permissionResourceMapper;

    /**
     * 未授权情况下是否拒绝，默认ture。
     * 未授权是指，资源找不到对应的Permission
     */
    @Setter
    private boolean refuseWhenUnauthorized = true;

    /**
     * 策略检查器工厂
     */
    @Setter
    private PolicyCheckerFactory policyCheckerFactory;

    /**
     * 校验参数
     */
    protected final P checkParam;

    /**
     * 校验参数中clientId对应的client
     */
    protected Client client;

    public AbstractAuthorizationChecker(P checkParam) {
        this.checkParam = checkParam;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public AuthorizationCheckResult<T> check() {
        try {
            // 设置client
            Client client = clientMapper.selectById(checkParam.getClientId());
            if (client == null) {
                throw new AuthorizationException("无法找到Client，clientId=" + checkParam.getClientId());
            }
            if (!client.getIsEnabled()) {
                throw new AuthorizationException("此Client未启用");
            }
            this.client = client;

            List<T> refused = Lists.newArrayList();
            List<T> allowed = Lists.newArrayList();
            List<T> resourceAssigner = checkParam.getResourceAssigner();

            ASSIGNER_ITER:
            for (T assigner : resourceAssigner) {
                // 找到待检测的资源
                List<Resource> findResources = assignCheckingResources(assigner);
                findResources = findResources == null ? new ArrayList<>() : findResources;
                // 过滤出开启的资源
                List<Resource> resources = findResources.stream().filter(Resource::getIsEnabled).collect(Collectors.toList());
                // 遍历资源依次拿到权限Permission
                List<PermissionVO> permissions = Lists.newArrayList();
                for (Resource resource : resources) {
                    permissions.addAll(findResourcePermissions(resource));
                }

                // 未授权的情况
                if (CollectionUtils.isEmpty(permissions)) {
                    if (refuseWhenUnauthorized) {
                        refused.add(assigner);
                    } else {
                        allowed.add(assigner);
                    }
                    continue;
                }

                // 对Permission的Intention进行分组
                Map<Intention, Set<PermissionVO>> intentGroupedPerms = permissions.stream()
                        .collect(Collectors.groupingBy(
                                permissionVO -> Intention.parse(permissionVO.getIntention()),
                                Collectors.mapping(Function.identity(), Collectors.toSet())
                        ));

                // 先检测 REFUSE 的权限，如果存在一个 REFUSE 的权限校验通过，则此资源约束被看作拒绝
                Set<PermissionVO> refusedPerms = intentGroupedPerms.getOrDefault(Intention.REFUSE, Sets.newHashSet());
                for (PermissionVO permission : refusedPerms) {
                    if (checkPermission(permission)) {
                        refused.add(assigner);
                        continue ASSIGNER_ITER;
                    }
                }

                // 再检测 ALLOW 的权限
                Set<PermissionVO> allowedPerms = intentGroupedPerms.getOrDefault(Intention.ALLOW, Sets.newHashSet());
                for (PermissionVO permission : allowedPerms) {
                    if (checkPermission(permission)) {
                        allowed.add(assigner);
                        continue ASSIGNER_ITER;
                    }
                }

                // 未授权时，根据配置决定是 refuse 还是 allow
                if (refuseWhenUnauthorized) {
                    refused.add(assigner);
                } else {
                    allowed.add(assigner);
                }
            }

            return new AuthorizationCheckResult<>(refused, allowed);

        } catch (Exception e) {
            throw new AuthorizationException(e.getMessage());
        }
    }

    /**
     * 决定资源约束对应着哪些资源。
     *
     * @param resourcesAssigner 资源约束对象，可以是资源名称、资源URI、资源Tag
     * @return 返回资源列表
     */
    protected abstract List<Resource> assignCheckingResources(T resourcesAssigner);


    /**
     * 找到资源关联的权限
     *
     * @return 资源关联的权限列表
     */
    protected List<PermissionVO> findResourcePermissions(Resource resource) {
        List<PermissionResource> permissionResources = permissionResourceMapper.selectList(Wrappers.<PermissionResource>lambdaQuery()
                .eq(PermissionResource::getResourceId, resource.getResourceId())
        );
        if (CollectionUtils.isEmpty(permissionResources)) {
            return Lists.newArrayList();
        }
        return permissionResources.stream()
                .map(permRsrc -> permissionService.get(client.getRealmId(), client.getClientId(), permRsrc.getPermissionId()))
                .collect(Collectors.toList());
    }


    /**
     * 进行Permission的校验
     *
     * @param permission 待校验的授权信息
     * @return 返回Permission校验是否通过
     */
    protected boolean checkPermission(PermissionVO permission) {
        // 检测权限是否禁用
        if (!permission.getIsEnabled()) {
            return false;
        }

        List<PermissionPolicyVO> policies = permission.getPolicys();
        if (CollectionUtils.isEmpty(policies)) {
            return false;
        }

        int allowedCount = 0;
        Logic logic = Logic.parse(permission.getLogic());
        if (logic == null) {
            throw new IllegalArgumentException("无法解析权限的策略，permission=" + permission);
        }

        // 逐个policy检查
        for (PermissionPolicyVO permPolicy : policies) {
            // 查找VO
            PolicyVO policy = policyService.get(client.getRealmId(), client.getClientId(), permPolicy.getPolicyId());
            Intention policyCheckIntention = policyCheckerFactory.newPolicyChecker(policy).check(checkParam);

            // 统计允许的policy个数
            if (Intention.ALLOW == policyCheckIntention) {
                allowedCount++;
            }
        }

        return logic.isSatisfied(policies.size(), allowedCount);
    }


}
