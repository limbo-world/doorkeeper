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

package org.limbo.doorkeeper.server.support.authc;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.api.constants.PolicyType;
import org.limbo.doorkeeper.api.model.param.auth.AuthenticationCheckParam;
import org.limbo.doorkeeper.api.model.vo.PermissionPolicyVO;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyParamVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyRoleVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyUserVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.dao.GroupRoleMapper;
import org.limbo.doorkeeper.server.dao.GroupUserMapper;
import org.limbo.doorkeeper.server.dao.UserRoleMapper;
import org.limbo.doorkeeper.server.entity.GroupRole;
import org.limbo.doorkeeper.server.entity.GroupUser;
import org.limbo.doorkeeper.server.entity.UserRole;
import org.limbo.doorkeeper.server.service.policy.PolicyService;
import org.limbo.doorkeeper.server.utils.EasyAntPathMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @date 2021/1/14 10:21 上午
 */
@Component
public abstract class AbstractAllowedExecutor<P extends AuthenticationCheckParam, T> {

    private static final ThreadLocal<EasyAntPathMatcher> PATH_MATCHER = ThreadLocal.withInitial(EasyAntPathMatcher::new);

    @Autowired
    private PolicyService policyService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private GroupUserMapper groupUserMapper;

    @Autowired
    private GroupRoleMapper groupRoleMapper;

    /**
     * 根据参数找到资源列表 循环每个资源 找到对应的权限和策略
     * 如果一个资源绑定了多个权限 当有一个权限拦截 则返回拦截 当没有一个权限放行 则也是拦截 否则 才进行放行
     */
    public abstract Map<Intention, List<T>> accessAllowed(Long userId, Long clientId, P p);

    /**
     * 执行权限判断
     */
    protected boolean permissionExecute(Long userId, Long realmId, Long clientId, PermissionVO permissionVO, Map<String, String> params) {
        // 权限已经被禁用了
        if (!permissionVO.getIsEnabled()) {
            return false;
        }
        Logic logic = Logic.parse(permissionVO.getLogic());
        int allow = 0;
        for (PermissionPolicyVO policy : permissionVO.getPolicys()) {
            PolicyVO policyVO = policyService.get(realmId, clientId, policy.getPolicyId());
            Intention policyIntention = policyExecute(userId, policyVO, params);
            if (Intention.ALLOW == policyIntention) {
                allow++;
            }
        }
        return logicResult(logic, permissionVO.getPolicys().size(), allow);
    }

    /**
     * 执行策略判断
     */
    protected Intention policyExecute(Long userId, PolicyVO policyVO, Map<String, String> params) {
        PolicyType type = PolicyType.parse(policyVO.getType());
        boolean pass = false;
        switch (type) {
            case ROLE:
                pass = rolePolicyExecute(userId, policyVO);
                break;
            case USER:
                pass = userPolicyExecute(userId, policyVO);
                break;
            case PARAM:
                pass = paramPolicyExecute(policyVO, params);
                break;
        }
        Intention intention = Intention.parse(policyVO.getIntention());
        return allowIntention(intention, pass);
    }

    protected Intention allowIntention(Intention intention, boolean pass) {
        if (Intention.ALLOW == intention) {
            if (pass) {
                return Intention.ALLOW;
            } else {
                return Intention.REFUSE;
            }
        } else {
            if (pass) {
                return Intention.REFUSE;
            } else {
                return Intention.ALLOW;
            }
        }
    }

    protected boolean rolePolicyExecute(Long userId, PolicyVO policyVO) {
        Logic logic = Logic.parse(policyVO.getLogic());
        // 策略绑定的角色 去除未启用的
        List<PolicyRoleVO> roles = policyVO.getRoles().stream().filter(PolicyRoleVO::getIsEnabled).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }
        List<Long> roleIds = roles.stream().map(PolicyRoleVO::getRoleId).collect(Collectors.toList());
        // 用户直接绑定的角色
        List<UserRole> userRoles = userRoleMapper.selectList(Wrappers.<UserRole>lambdaQuery()
                .eq(UserRole::getUserId, userId)
                .in(UserRole::getRoleId, roleIds)
        );
        Set<Long> userRoleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        // 用户所在用户组的角色
        List<GroupUser> groupUsers = groupUserMapper.selectList(Wrappers.<GroupUser>lambdaQuery()
                .eq(GroupUser::getUserId, userId)
        );
        if (CollectionUtils.isNotEmpty(groupUsers)) {
            List<GroupRole> groupRoles = groupRoleMapper.selectList(Wrappers.<GroupRole>lambdaQuery()
                    .in(GroupRole::getGroupId, groupUsers.stream().map(GroupUser::getGroupId).collect(Collectors.toList()))
            );
            if (CollectionUtils.isNotEmpty(groupRoles)) {
                userRoleIds.addAll(groupRoles.stream().map(GroupRole::getRoleId).filter(roleIds::contains).collect(Collectors.toSet()));
            }
        }
        return logicResult(logic, roleIds.size(), userRoleIds.size());
    }

    protected boolean userPolicyExecute(Long userId, PolicyVO policyVO) {
        List<PolicyUserVO> users = policyVO.getUsers();
        List<PolicyUserVO> ul = users.stream()
                .filter(user -> userId.equals(user.getUserId())).collect(Collectors.toList());
        return CollectionUtils.isNotEmpty(ul);
    }

    protected boolean paramPolicyExecute(PolicyVO policyVO, Map<String, String> params) {
        Logic logic = Logic.parse(policyVO.getLogic());
        List<PolicyParamVO> policyParams = policyVO.getParams();
        Map<String, String> policyParamMap = new HashMap<>();
        for (PolicyParamVO policyParam : policyParams) {
            policyParamMap.put(policyParam.getK(), policyParam.getV());
        }
        int have = 0;
        for (Map.Entry<String, String> entry : policyParamMap.entrySet()) {
            String v = params.get(entry.getKey());
            if (v != null && v.equals(entry.getValue())) {
                have++;
            }
        }
        return logicResult(logic, policyParamMap.size(), have);
    }

    public boolean logicResult(Logic logic, int options, int have) {
        // 有些情况 比如配置角色策略，但是角色都禁用了
        if (options <= 0) {
            return false;
        }
        switch (logic) {
            case ALL:
                if (options == have) {
                    return true;
                }
                break;
            case ONE:
                if (have > 0) {
                    return true;
                }
                break;
            case MORE:
                if (have > options - have) {
                    return true;
                }
                break;
        }
        return false;
    }

    public static void main(String[] args) {
        UriAllowExecutor executor = new UriAllowExecutor();
        System.out.println(executor.pathMatch("/admin/realm/**", "/admin/realm"));
        ;
        System.out.println(executor.pathMatch("/admin/realm/**", "/admin/"));
        ;

        System.out.println(executor.pathMatch("/admin/realm/*/**", "/admin/realm"));
        ;
        System.out.println(executor.pathMatch("/admin/realm/*/**", "/admin/realm/8"));
        ;
        System.out.println(executor.pathMatch("/admin/realm/*/**", "/admin/realm/8/user"));
        ;
    }

    public boolean pathMatch(String pattern, String path) {
        EasyAntPathMatcher antPathMatcher = PATH_MATCHER.get();
        return antPathMatcher.match(StringUtils.trim(pattern), StringUtils.trim(path));
    }

}
