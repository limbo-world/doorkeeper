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

package org.limbo.doorkeeper.server.useless;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.api.constants.PolicyType;
import org.limbo.doorkeeper.api.dto.vo.GroupVO;
import org.limbo.doorkeeper.api.dto.vo.policy.PolicyGroupVO;
import org.limbo.doorkeeper.api.dto.vo.policy.PolicyRoleVO;
import org.limbo.doorkeeper.api.dto.vo.policy.PolicyUserVO;
import org.limbo.doorkeeper.api.dto.vo.policy.PolicyVO;
import org.limbo.doorkeeper.core.domian.policy.GroupTreeDO;
import org.limbo.doorkeeper.infrastructure.exception.AuthorizationException;
import org.limbo.doorkeeper.infrastructure.po.*;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.GroupMapper;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.GroupRoleMapper;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.GroupUserMapper;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.UserRoleMapper;
import org.limbo.utils.jackson.JacksonUtils;
import org.limbo.utils.reflection.EnhancedBeanUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @since 2021/6/2 10:57 上午
 */
@Slf4j
public class PolicyChecker {

    private final GroupMapper groupMapper;

    private final GroupUserMapper groupUserMapper;

    private final GroupRoleMapper groupRoleMapper;

    private final UserRoleMapper userRoleMapper;

    private final UserPO user;

    private List<GroupUserPO> groupUsers;
    /**
     * 用户拥有的角色ID 包含用户直接绑定的 以及用户所在用户组相关联的
     */
    private Set<Long> userRoleIds;

    private GroupTreeDO groupTree;

    public PolicyChecker(UserPO user, GroupMapper groupMapper, GroupUserMapper groupUserMapper, GroupRoleMapper groupRoleMapper,
                         UserRoleMapper userRoleMapper) {
        this.user = user;
        this.groupMapper = groupMapper;
        this.groupUserMapper = groupUserMapper;
        this.groupRoleMapper = groupRoleMapper;
        this.userRoleMapper = userRoleMapper;
    }

    /**
     * 校验策略是否通过
     *
     * @param policy 策略
     * @return 是否通过
     */
    public Intention check(PolicyVO policy) {
        PolicyType policyType = PolicyType.parse(policy.getType());
        if (policyType == null) {
            throw new AuthorizationException("解析policy类型失败，类型" + policy.getType());
        }
        // 用户没启用的情况
        Intention intention = Intention.parse(policy.getIntention());
        if (!user.getIsEnabled()) {
            return reverseIntentionIfNotPassed(intention, false);
        }
        if (!policy.getIsEnabled()) {
            return reverseIntentionIfNotPassed(intention, false);
        }
        // 根据策略类型判断逻辑
        boolean checkPassed;
        try {
            switch (policyType) {
                case ROLE:
                    checkPassed = doRoleCheck(policy);
                    break;
                case USER:
                    checkPassed = doUserCheck(policy);
                    break;
                case GROUP:
                    checkPassed = doGroupCheck(policy);
                    break;
                default:
                    throw new AuthorizationException("不支持的策略类型:" + policyType);
            }
        } catch (Exception e) {
            log.error("策略校验失败 " + JacksonUtils.toJSONString(policy));
            throw e;
        }
        return reverseIntentionIfNotPassed(intention, checkPassed);
    }

    private boolean doRoleCheck(PolicyVO policy) {
        // 策略绑定的角色 去除未启用的
        Set<Long> checkRoleIds = policy.getRoles().stream()
                .filter(PolicyRoleVO::getIsEnabled)
                .map(PolicyRoleVO::getRoleId)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(checkRoleIds)) {
            return false;
        }

        List<Long> includeRoleIds = new ArrayList<>();
        for (Long checkRoleId : checkRoleIds) {
            if (getUserRoleIds().contains(checkRoleId)) {
                includeRoleIds.add(checkRoleId);
            }
        }
        // 解析策略逻辑，判断是否满足逻辑条件
        return Logic.isSatisfied(getPolicyLogic(policy), checkRoleIds.size(),
                CollectionUtils.isEmpty(includeRoleIds) ? 0 : includeRoleIds.size());
    }

    /**
     * 判断当前用户所在用户组是否满足策略
     *
     * @param policy
     * @return
     */
    private boolean doGroupCheck(PolicyVO policy) {
        List<GroupUserPO> groupUsers = getGroupUsers();
        GroupTreeDO groupTree = getGroupTree();
        if (CollectionUtils.isEmpty(groupUsers)) {
            return false;
        }
        // 获取满足策略的用户组ID
        Set<Long> includeGroupIds = new HashSet<>();
        for (PolicyGroupVO policyGroup : policy.getGroups()) {
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
        for (GroupUserPO groupUser : groupUsers) {
            if (includeGroupIds.contains(groupUser.getGroupId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前用户是否满足策略
     *
     * @param policy
     * @return
     */
    private boolean doUserCheck(PolicyVO policy) {
        return user != null && user.getIsEnabled() && policy.getUsers().stream()
                .map(PolicyUserVO::getUserId)
                .anyMatch(uid -> Objects.equals(uid, user.getUserId()));
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

    /**
     * 传递角色
     */
    private void extendRole(Map<Long, List<Long>> groupRoleMap, Long roleId, List<GroupVO> children) {
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        for (GroupVO child : children) {
            groupRoleMap.computeIfAbsent(child.getGroupId(), k -> new ArrayList<>());
            groupRoleMap.get(child.getGroupId()).add(roleId);
            // 继续传递
            extendRole(groupRoleMap, roleId, child.getChildren());
        }
    }

    /**
     * 当策略检查结果为未通过时，将intention反转。<br/>
     * 即，当checkPassed为false时，入参intention是ALLOW就返回REFUSE，是REFUSE就返回ALLOW。<br/>
     * 当checkPassed为true时，返回入参intention。<br/>
     *
     * @param intention   检测的策略intention
     * @param checkPassed 策略检测是否通过
     */
    private Intention reverseIntentionIfNotPassed(Intention intention, boolean checkPassed) {
        if (intention == Intention.ALLOW) {
            return checkPassed ? Intention.ALLOW : Intention.REFUSE;
        } else if (intention == Intention.REFUSE) {
            return checkPassed ? Intention.REFUSE : Intention.ALLOW;
        } else {
            throw new IllegalArgumentException("intention 为null");
        }
    }

    private Logic getPolicyLogic(PolicyVO policy) {
        if (policy == null) {
            throw new IllegalArgumentException("policy为null");
        }

        Logic logic = Logic.parse(policy.getLogic());
        if (logic == null) {
            throw new IllegalArgumentException("无法解析的逻辑，policy=" + policy);
        }
        return logic;
    }

    public List<GroupUserPO> getGroupUsers() {
        if (groupUsers == null) {
            groupUsers = groupUserMapper.selectList(Wrappers.<GroupUserPO>lambdaQuery()
                    .eq(GroupUserPO::getUserId, user.getUserId())
            );
        }
        return groupUsers;
    }

    public Set<Long> getUserRoleIds() {
        if (userRoleIds != null) {
            return userRoleIds;
        }

        userRoleIds = new HashSet<>();

        // 获取用户角色
        List<UserRolePO> userRolePOS = userRoleMapper.selectList(Wrappers.<UserRolePO>lambdaQuery()
                .eq(UserRolePO::getUserId, user.getUserId())
        );

        if (CollectionUtils.isEmpty(userRolePOS)) {
            userRolePOS = new ArrayList<>();
        }

        // 用户拥有的角色ID
        userRoleIds.addAll(userRolePOS.stream().map(UserRolePO::getRoleId).collect(Collectors.toList()));

        // 获取用户组角色
        List<GroupRolePO> groupRoles = groupRoleMapper.selectList(Wrappers.<GroupRolePO>lambdaQuery()
                .in(GroupRolePO::getGroupId, getGroupTree().getList().stream().map(GroupVO::getGroupId).collect(Collectors.toSet()))
        );
        if (CollectionUtils.isEmpty(groupRoles)) {
            return userRoleIds;
        }

        // 获取用户用户组关系
        List<GroupUserPO> groupUsers = groupUserMapper.selectList(Wrappers.<GroupUserPO>lambdaQuery()
                .eq(GroupUserPO::getUserId, user.getUserId())
        );
        if (CollectionUtils.isEmpty(groupUsers)) {
            return userRoleIds;
        }

        // 判断每个组有哪些角色
        Map<Long, List<Long>> groupRoleMap = new HashMap<>();
        for (GroupRolePO groupRole : groupRoles) {
            GroupVO group = groupTree.findFromTree(groupRole.getGroupId());
            if (group == null) {
                continue;
            }
            groupRoleMap.computeIfAbsent(group.getGroupId(), k -> new ArrayList<>());
            groupRoleMap.get(group.getGroupId()).add(groupRole.getRoleId());
            // 是否向下延伸
            if (groupRole.getIsExtend()) {
                extendRole(groupRoleMap, groupRole.getRoleId(), group.getChildren());
            }
        }
        // 用户对应组的角色
        for (GroupUserPO groupUser : groupUsers) {
            List<Long> roleIds = groupRoleMap.get(groupUser.getGroupId());
            if (CollectionUtils.isNotEmpty(roleIds)) {
                userRoleIds.addAll(roleIds);
            }
        }

        return userRoleIds;
    }

    public GroupTreeDO getGroupTree() {
        if (groupTree == null) {
            // 获取用户所在用户组
            List<GroupPO> groups = groupMapper.selectList(Wrappers.<GroupPO>lambdaQuery()
                    .eq(GroupPO::getRealmId, user.getRealmId())
            );
            // 生成组织树
            List<GroupVO> groupVOS = EnhancedBeanUtils.createAndCopyList(groups, GroupVO.class);
            groupTree = GroupTreeDO.create(groupVOS);
        }
        return groupTree;
    }


}
