package org.limbo.doorkeeper.server.domain.policy;

import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.api.dto.vo.policy.PolicyRoleVO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @since 2021/11/30
 */
public class RolePolicy extends PolicyEntity<Long> {

    private List<PolicyRoleVO> roles;

    public RolePolicy(List<PolicyRoleVO> roles) {
        this.roles = roles;
    }

    @Override
    protected boolean doResult(Collection<Long> roleIds) {
        // 策略绑定的角色 去除未启用的
        Set<Long> checkRoleIds = roles.stream()
                .filter(PolicyRoleVO::getIsEnabled)
                .map(PolicyRoleVO::getRoleId)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(checkRoleIds)) {
            return false;
        }

        List<Long> includeRoleIds = new ArrayList<>();
        for (Long checkRoleId : checkRoleIds) {
            if (roleIds.contains(checkRoleId)) {
                includeRoleIds.add(checkRoleId);
            }
        }
        // 解析策略逻辑，判断是否满足逻辑条件
        return Logic.isSatisfied(logic, checkRoleIds.size(),
                CollectionUtils.isEmpty(includeRoleIds) ? 0 : includeRoleIds.size());
    }
}
