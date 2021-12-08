package org.limbo.doorkeeper.server.domain.policy;

import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.dto.vo.GroupVO;
import org.limbo.doorkeeper.api.dto.vo.policy.PolicyGroupVO;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Devil
 * @since 2021/11/30
 */
public class GroupPolicy extends PolicyEntity<Long> {
    // 策略设置的组
    private List<PolicyGroupVO> groups;
    // 所有组
    private GroupTreeDO groupTree;

    public GroupPolicy(List<PolicyGroupVO> groups, GroupTreeDO groupTree) {
        this.groups = groups;
        this.groupTree = groupTree;
    }

    @Override
    protected boolean doResult(Collection<Long> groupIds) {
        // 获取满足策略的用户组ID
        Set<Long> includeGroupIds = new HashSet<>();
        for (PolicyGroupVO policyGroup : groups) {
            GroupVO group = groupTree.findFromTree(policyGroup.getGroupId());
            if (group == null) {
                continue;
            }
            includeGroupIds.add(group.getGroupId());
            // 是否向下延伸
            if (policyGroup.getIsExtend()) {
                extendGroupId(includeGroupIds, group.getChildren());
            }
        }
        // 判断用户是否在对应用户组
        for (Long groupId : groupIds) {
            if (includeGroupIds.contains(groupId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将所有子组放入集合
     */
    private void extendGroupId(Collection<Long> groupIds, List<GroupVO> children) {
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        for (GroupVO child : children) {
            groupIds.add(child.getGroupId());
            // 继续传递
            extendGroupId(groupIds, child.getChildren());
        }
    }
}
