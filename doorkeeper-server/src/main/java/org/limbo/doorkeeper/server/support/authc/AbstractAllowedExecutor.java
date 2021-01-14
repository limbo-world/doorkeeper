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
import org.limbo.doorkeeper.server.dao.UserRoleMapper;
import org.limbo.doorkeeper.server.entity.UserRole;
import org.limbo.doorkeeper.server.service.policy.PolicyService;
import org.limbo.doorkeeper.server.utils.EasyAntPathMatcher;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @date 2021/1/14 10:21 上午
 */
public abstract class AbstractAllowedExecutor<P extends AuthenticationCheckParam, T> {

    private static final ThreadLocal<EasyAntPathMatcher> PATH_MATCHER = ThreadLocal.withInitial(EasyAntPathMatcher::new);

    @Autowired
    private PolicyService policyService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    public abstract Map<Intention, List<T>> accessAllowedByName(Long userId, Long clientId, P p);

    protected boolean permissionExecute(Long userId, Long realmId, Long clientId, PermissionVO permissionVO, Map<String, String> params) {
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
        List<PolicyRoleVO> roles = policyVO.getRoles();
        List<Long> roleIds = roles.stream().map(PolicyRoleVO::getRoleId).collect(Collectors.toList());
        List<UserRole> userRoles = userRoleMapper.selectList(Wrappers.<UserRole>lambdaQuery()
                .eq(UserRole::getUserId, userId)
                .in(UserRole::getRoleId, roleIds)
        );
        return logicResult(logic, roleIds.size(), userRoles == null ? 0 : userRoles.size());
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

    public boolean pathMatch(String pattern, String path) {
        EasyAntPathMatcher antPathMatcher = PATH_MATCHER.get();
        return antPathMatcher.match(StringUtils.trim(pattern), StringUtils.trim(path));
    }

}
