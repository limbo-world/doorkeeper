package org.limbo.doorkeeper.core.component.checker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.core.domian.aggregate.Permission;
import org.limbo.doorkeeper.core.domian.policy.Policy;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 资源校验器
 *
 * @author yuansheng
 * @since 2022/2/2
 */
@Slf4j
public class ResourceChecker extends AbstractChecker {

    /**
     * 未授权情况下是否拒绝，未授权是指，资源找不到对应的Permission
     */
    boolean REFUSE_WHEN_UNAUTHORIZED = true;

    private List<Long> resourceIds;

    private Map<Long, List<Long>> resourcePermissionRelationMap;

    private Map<Long, Permission> permissionMap;

    private Map<Long, Policy> policyMap;

    @Override
    public CheckResult<List<Long>> check() {
        if (!user.isEnabled() || CollectionUtils.isEmpty(resourceIds)) {
            return emptyResult();
        }

        List<Long> result = new ArrayList<>();
        ASSIGNER_ITER:
        for (Long resourceId : resourceIds) {
            List<Long> permissionIds = resourcePermissionRelationMap.get(resourceId);
            if (CollectionUtils.isEmpty(permissionIds)) {
                if (REFUSE_WHEN_UNAUTHORIZED) {
                    continue;
                } else {
                    result.add(resourceId);
                }
            }

            // 获取资源权限
            List<Permission> permissionAggregates = new ArrayList<>();
            for (Long permissionId : permissionIds) {
                if (permissionMap.containsKey(permissionId)) {
                    permissionAggregates.add(permissionMap.get(permissionId));
                }
            }
            if (CollectionUtils.isEmpty(permissionAggregates)) {
                if (REFUSE_WHEN_UNAUTHORIZED) {
                    continue;
                } else {
                    result.add(resourceId);
                }
            }

            // 对Permission的Intention进行分组
            Map<Intention, Set<Permission>> intentGroupedPerms = permissionAggregates.stream().collect(Collectors.groupingBy(
                    Permission::getIntention,
                    Collectors.mapping(Function.identity(), Collectors.toSet())
            ));

            // 先检测 REFUSE 的权限，如果存在一个 REFUSE 的权限校验通过，则此资源约束被看作拒绝
            Set<Permission> refusedPerms = intentGroupedPerms.getOrDefault(Intention.REFUSE, new HashSet<>());
            for (Permission permissionAggregate : refusedPerms) {
                if (checkPermissionLogic(permissionAggregate)) {
                    continue ASSIGNER_ITER;
                }
            }
            // 再检测 ALLOW 的权限
            Set<Permission> allowedPerms = intentGroupedPerms.getOrDefault(Intention.ALLOW, new HashSet<>());
            for (Permission permissionAggregate : allowedPerms) {
                if (checkPermissionLogic(permissionAggregate)) {
                    result.add(resourceId);
                    continue ASSIGNER_ITER;
                }
            }
        }
        return efficientResult(result);
    }

    /**
     * 进行Permission的校验
     *
     * @param permissionAggregate 待校验的授权信息
     * @return 返回Permission校验是否通过
     */
    private boolean checkPermissionLogic(Permission permissionAggregate) {
        // 检测权限是否禁用
        if (!permissionAggregate.isEnabled()) {
            return false;
        }

        if (CollectionUtils.isEmpty(permissionAggregate.getPolicyIds())) {
            return false;
        }

        // 逐个policy检查
        int allowedCount = 0;
        int totalCount = 0;
        for (Long policyId : permissionAggregate.getPolicyIds()) {
            Policy policy = policyMap.get(policyId);
            if (policy == null) {
                continue;
            }
            totalCount++;
            // 统计允许的policy个数
            switch (policy.getType()) {
                case USER:
                    // todo
                    if (policy.intention(Collections.singletonList(null)).allow()) {
                        allowedCount++;
                    }
                    break;
                case ROLE:
                    break;
                case GROUP:
                    break;
                default:
                    break;
            }
        }

        return Logic.isSatisfied(permissionAggregate.getLogic(), totalCount, allowedCount);
    }

    /**
     * 根据决策判断在未授权情况返回所有资源还是空
     */
    private CheckResult<List<Long>> checkResourceRefuseResult(List<Long> resourceIds) {
        if (!REFUSE_WHEN_UNAUTHORIZED) {
            return efficientResult(resourceIds);
        }
        return emptyResult();
    }

    /**
     * 返回结果
     */
    private CheckResult<List<Long>> efficientResult(List<Long> resourceIds) {
        return CheckResult.<List<Long>>builder()
                .result(resourceIds)
                .build();
    }

    /**
     * 返回空结果
     */
    private CheckResult<List<Long>> emptyResult() {
        return CheckResult.<List<Long>>builder()
                .result(new ArrayList<>())
                .build();
    }
}
