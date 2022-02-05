package org.limbo.doorkeeper.core.domain.component.checker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.common.constant.DoorkeeperConstants;
import org.limbo.doorkeeper.common.exception.AuthorizationException;
import org.limbo.doorkeeper.core.domain.entity.IDRelation;
import org.limbo.doorkeeper.core.domain.entity.Permission;
import org.limbo.doorkeeper.core.domain.entity.policy.Policy;

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

    private List<Long> resourceIds;

    private Map<Long, IDRelation> resourcePermissionRelationMap;

    private Map<Long, Permission> permissionMap;

    private Map<Long, Policy> policyMap;

    @Override
    public CheckResult<List<Long>> check() {
        if (user.isDisabled() || CollectionUtils.isEmpty(resourceIds)) {
            return emptyResult();
        }

        try {
            List<Long> result = new ArrayList<>();
            ASSIGNER_ITER:
            for (Long resourceId : resourceIds) {
                List<Long> permissionIds = Optional.ofNullable(resourcePermissionRelationMap.get(resourceId))
                        .orElse(new IDRelation()).getRelatedIds();
                if (CollectionUtils.isEmpty(permissionIds)) {
                    if (DoorkeeperConstants.REFUSE_WHEN_UNAUTHORIZED) {
                        continue;
                    } else {
                        result.add(resourceId);
                    }
                }

                // 获取资源权限
                List<Permission> permissions = new ArrayList<>();
                for (Long permissionId : permissionIds) {
                    if (permissionMap.containsKey(permissionId)) {
                        permissions.add(permissionMap.get(permissionId));
                    }
                }
                if (CollectionUtils.isEmpty(permissions)) {
                    if (DoorkeeperConstants.REFUSE_WHEN_UNAUTHORIZED) {
                        continue;
                    } else {
                        result.add(resourceId);
                    }
                }

                // 对Permission的Intention进行分组
                Map<Intention, Set<Permission>> intentGroupedPerms = permissions.stream().collect(Collectors.groupingBy(
                        Permission::getIntention,
                        Collectors.mapping(Function.identity(), Collectors.toSet())
                ));

                // 先检测 REFUSE 的权限，如果存在一个 REFUSE 的权限校验通过，则此资源约束被看作拒绝
                Set<Permission> refusedPerms = intentGroupedPerms.getOrDefault(Intention.REFUSE, new HashSet<>());
                for (Permission permission : refusedPerms) {
                    if (checkPermissionLogic(permission)) {
                        continue ASSIGNER_ITER;
                    }
                }
                // 再检测 ALLOW 的权限
                Set<Permission> allowedPerms = intentGroupedPerms.getOrDefault(Intention.ALLOW, new HashSet<>());
                for (Permission permission : allowedPerms) {
                    if (checkPermissionLogic(permission)) {
                        result.add(resourceId);
                        continue ASSIGNER_ITER;
                    }
                }
            }
            return efficientResult(result);

        } catch (Exception e) {
            log.error("鉴权校验失败", e);
            throw new AuthorizationException(e.getMessage());
        }
    }

    /**
     * 进行Permission的校验
     *
     * @param permission 待校验的授权信息
     * @return 返回Permission校验是否通过
     */
    private boolean checkPermissionLogic(Permission permission) {
        // 检测权限是否禁用
        if (!permission.isEnabled()) {
            return false;
        }

        if (CollectionUtils.isEmpty(permission.getPolicyIds())) {
            return false;
        }

        // 逐个policy检查
        int allowedCount = 0;
        int totalCount = 0;
        for (Long policyId : permission.getPolicyIds()) {
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

        return Logic.isSatisfied(permission.getLogic(), totalCount, allowedCount);
    }

    /**
     * 根据决策判断在未授权情况返回所有资源还是空
     */
    private CheckResult<List<Long>> checkResourceRefuseResult(List<Long> resourceIds) {
        if (!DoorkeeperConstants.REFUSE_WHEN_UNAUTHORIZED) {
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
