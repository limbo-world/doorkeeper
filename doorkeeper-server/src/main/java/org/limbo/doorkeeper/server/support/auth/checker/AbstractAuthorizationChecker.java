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
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.api.model.param.check.AuthorizationCheckParam;
import org.limbo.doorkeeper.api.model.vo.PermissionPolicyVO;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.limbo.doorkeeper.api.model.vo.ResourceVO;
import org.limbo.doorkeeper.api.model.vo.check.AuthorizationCheckResult;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.dal.dao.PermissionDao;
import org.limbo.doorkeeper.server.dal.dao.PolicyDao;
import org.limbo.doorkeeper.server.dal.entity.Client;
import org.limbo.doorkeeper.server.dal.entity.PermissionResource;
import org.limbo.doorkeeper.server.dal.mapper.ClientMapper;
import org.limbo.doorkeeper.server.dal.mapper.PermissionResourceMapper;
import org.limbo.doorkeeper.server.support.auth.AuthorizationException;
import org.limbo.doorkeeper.server.support.auth.policies.PolicyCheckerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 目前非线程安全，每次校验需要生成一个新的
 *
 * @author brozen
 * @date 2021/1/14
 */
@Slf4j
public abstract class AbstractAuthorizationChecker<P extends AuthorizationCheckParam<T>, T> implements AuthorizationChecker<P, T> {

    @Setter
    private PermissionDao permissionDao;

    @Setter
    private PolicyDao policyDao;

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
    public AuthorizationCheckResult check() {
        try {
            List<ResourceVO> result = new ArrayList<>();

            // 找到待检测的启用资源
            List<ResourceVO> findResources = assignCheckingResources(checkParam.getResourceAssigner());
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
     * @param resourcesAssigner 资源约束对象，可以是资源名称、资源URI、资源Tag
     * @return 返回资源列表
     */
    protected abstract List<ResourceVO> assignCheckingResources(List<T> resourcesAssigner);


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


}
