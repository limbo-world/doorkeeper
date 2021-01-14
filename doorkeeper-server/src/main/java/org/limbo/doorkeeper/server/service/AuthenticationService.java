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

package org.limbo.doorkeeper.server.service;

import org.limbo.doorkeeper.server.dao.ClientMapper;
import org.limbo.doorkeeper.server.dao.PermissionResourceMapper;
import org.limbo.doorkeeper.server.dao.ResourceMapper;
import org.limbo.doorkeeper.server.dao.UserRoleMapper;
import org.limbo.doorkeeper.server.service.policy.PolicyService;
import org.limbo.doorkeeper.server.utils.EasyAntPathMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Devil
 * @date 2021/1/13 3:29 下午
 */
@Service
public class AuthenticationService {

    private static final ThreadLocal<EasyAntPathMatcher> PATH_MATCHER = ThreadLocal.withInitial(EasyAntPathMatcher::new);

    @Autowired
    private PolicyService policyService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private PermissionResourceMapper permissionResourceMapper;

    @Autowired
    private PermissionService permissionService;

    /**
     * 检查是否有权访问资源
     */
//    public boolean accessAllowed(List<PermissionVO> permissions) {
//        List<PermissionVO> permissions = getPermissionVOS(param);
//        if (CollectionUtils.isEmpty(permissions)) {
//            return false;
//        }
//
//        Map<String, Set<PermissionVO>> intentionPermissions = permissions.stream().collect(Collectors.groupingBy(
//                PermissionVO::getIntention,
//                Collectors.mapping(p -> p, Collectors.toSet())
//        ));
//
//        // 找到拦截的权限 如果满足 返回false
//        Set<PermissionVO> refuse = intentionPermissions.get(Intention.REFUSE);
//        for (PermissionVO permissionVO : refuse) {
//            if (permissionExecute(param.getUserId(), permissionVO, param.getParams())) {
//                return false;
//            }
//        }
//
//        // 找到放行的权限 如果满足 返回true
//        Set<PermissionVO> allow = intentionPermissions.get(Intention.REFUSE);
//        for (PermissionVO permissionVO : allow) {
//            if (permissionExecute(param.getUserId(), permissionVO, param.getParams())) {
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * 成功或者失败 list里面是资源名称
     */
//    public Map<Intention, List<String>> accessAllowedByName(AuthenticationNameCheckParam param) {
//        // 获取对应的client
//        Client client = clientMapper.selectById(param.getClientId());
//
//        List<Resource> resources = resourceMapper.selectList(Wrappers.<Resource>lambdaQuery()
//                .eq(Resource::getRealmId, client.getRealmId())
//                .eq(Resource::getClientId, client.getClientId())
//                .in(Resource::getName, param.getNames())
//        );
//
//        Map<Intention, List<String>> result = new HashMap<>();
//        result.put(Intention.ALLOW, new ArrayList<>());
//        result.put(Intention.REFUSE, new ArrayList<>());
//
//        if (CollectionUtils.isEmpty(resources)) {
//            result.put(Intention.REFUSE, param.getNames());
//            return result;
//        }
//
//        for (Resource resource : resources) {
//            List<PermissionResource> permissionResources = permissionResourceMapper.selectList(Wrappers.<PermissionResource>lambdaQuery()
//                    .eq(PermissionResource::getResourceId, resource.getResourceId())
//            );
//            if (CollectionUtils.isEmpty(permissionResources)) {
//                result.get(Intention.REFUSE).add(resource.getName());
//                continue;
//            }
//            Set<Long> permissionIds = permissionResources.stream().map(PermissionResource::getPermissionId).collect(Collectors.toSet());
//            // 找到权限以及对应的策略
//            List<PermissionVO> permissions = new ArrayList<>();
//            for (Long permissionId : permissionIds) {
//                PermissionVO permissionVO = permissionService.get(client.getRealmId(), client.getClientId(), permissionId);
//                if (permissionVO != null) {
//                    permissions.add(permissionVO);
//                }
//            }
//            if (CollectionUtils.isEmpty(permissions)) {
//                result.get(Intention.REFUSE).add(resource.getName());
//                continue;
//            }
//            Map<String, Set<PermissionVO>> intentionPermissions = permissions.stream().collect(Collectors.groupingBy(
//                    PermissionVO::getIntention,
//                    Collectors.mapping(p -> p, Collectors.toSet())
//            ));
//
//            // 找到拦截的权限 如果满足 返回false
//            Set<PermissionVO> refuse = intentionPermissions.get(Intention.REFUSE);
//            if (CollectionUtils.isNotEmpty(refuse)) {
//                boolean r = false;
//                for (PermissionVO permissionVO : refuse) {
//                    if (permissionExecute(param.getUserId(), client.getRealmId(), client.getClientId(), permissionVO, param.getParams())) {
//                        r = true;
//                        break;
//                    }
//                }
//                if (r) {
//                    result.get(Intention.REFUSE).add(resource.getName());
//                    continue;
//                }
//            }
//
//
//            // 找到放行的权限 如果满足 返回true
//            Set<PermissionVO> allow = intentionPermissions.get(Intention.ALLOW);
//            if (CollectionUtils.isNotEmpty(allow)) {
//                boolean a = false;
//                for (PermissionVO permissionVO : allow) {
//                    if (permissionExecute(param.getUserId(), client.getRealmId(), client.getClientId(), permissionVO, param.getParams())) {
//                        a = true;
//                        break;
//                    }
//                }
//                if (a) {
//                    result.get(Intention.ALLOW).add(resource.getName());
//                    continue;
//                }
//            }
//
//            result.get(Intention.REFUSE).add(resource.getName());
//
//        }
//
//        return result;
//    }

//    private boolean permissionExecute(Long userId, Long realmId, Long clientId, PermissionVO permissionVO, Map<String, String> params) {
//        Logic logic = Logic.parse(permissionVO.getLogic());
//        int allow = 0;
//        for (PermissionPolicyVO policy : permissionVO.getPolicys()) {
//            PolicyVO policyVO = policyService.get(realmId, clientId, policy.getPolicyId());
//            Intention policyIntention = policyExecute(userId, policyVO, params);
//            if (Intention.ALLOW == policyIntention) {
//                allow++;
//            }
//        }
//        return logicResult(logic, permissionVO.getPolicys().size(), allow);
//    }
//
//    private Intention policyExecute(Long userId, PolicyVO policyVO, Map<String, String> params) {
//        PolicyType type = PolicyType.parse(policyVO.getType());
//        boolean pass = false;
//        switch (type) {
//            case ROLE:
//                pass = rolePolicyExecute(userId, policyVO);
//                break;
//            case USER:
//                pass = userPolicyExecute(userId, policyVO);
//                break;
//            case PARAM:
//                pass = paramPolicyExecute(policyVO, params);
//                break;
//        }
//        Intention intention = Intention.parse(policyVO.getIntention());
//        return allowIntention(intention, pass);
//    }
//
//    private Intention allowIntention(Intention intention, boolean pass) {
//        if (Intention.ALLOW == intention) {
//            if (pass) {
//                return Intention.ALLOW;
//            } else {
//                return Intention.REFUSE;
//            }
//        } else {
//            if (pass) {
//                return Intention.REFUSE;
//            } else {
//                return Intention.ALLOW;
//            }
//        }
//    }
//
//    private boolean rolePolicyExecute(Long userId, PolicyVO policyVO) {
//        Logic logic = Logic.parse(policyVO.getLogic());
//        List<PolicyRoleVO> roles = policyVO.getRoles();
//        List<Long> roleIds = roles.stream().map(PolicyRoleVO::getRoleId).collect(Collectors.toList());
//        List<UserRole> userRoles = userRoleMapper.selectList(Wrappers.<UserRole>lambdaQuery()
//                .eq(UserRole::getUserId, userId)
//                .in(UserRole::getRoleId, roleIds)
//        );
//        return logicResult(logic, roleIds.size(), userRoles == null ? 0 : userRoles.size());
//    }
//
//    private boolean userPolicyExecute(Long userId, PolicyVO policyVO) {
//        List<PolicyUserVO> users = policyVO.getUsers();
//        List<PolicyUserVO> ul = users.stream()
//                .filter(user -> userId.equals(user.getUserId())).collect(Collectors.toList());
//        return CollectionUtils.isNotEmpty(ul);
//    }
//
//    private boolean paramPolicyExecute(PolicyVO policyVO, Map<String, String> params) {
//        Logic logic = Logic.parse(policyVO.getLogic());
//        List<PolicyParamVO> policyParams = policyVO.getParams();
//        Map<String, String> policyParamMap = new HashMap<>();
//        for (PolicyParamVO policyParam : policyParams) {
//            policyParamMap.put(policyParam.getK(), policyParam.getV());
//        }
//        int have = 0;
//        for (Map.Entry<String, String> entry : policyParamMap.entrySet()) {
//            String v = params.get(entry.getKey());
//            if (v != null && v.equals(entry.getValue())) {
//                have++;
//            }
//        }
//        return logicResult(logic, policyParamMap.size(), have);
//    }
//
//    private boolean logicResult(Logic logic, int options, int have) {
//        switch (logic) {
//            case ALL:
//                if (options == have) {
//                    return true;
//                }
//                break;
//            case ONE:
//                if (have > 0) {
//                    return true;
//                }
//                break;
//            case MORE:
//                if (have > options - have) {
//                    return true;
//                }
//                break;
//        }
//        return false;
//    }
//
//    public boolean pathMatch(String pattern, String path) {
//        EasyAntPathMatcher antPathMatcher = PATH_MATCHER.get();
//        return antPathMatcher.match(StringUtils.trim(pattern), StringUtils.trim(path));
//    }

}
