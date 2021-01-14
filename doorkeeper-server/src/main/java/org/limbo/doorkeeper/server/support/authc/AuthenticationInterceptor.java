///*
// * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * 	http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package org.limbo.doorkeeper.server.support.authc;
//
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections4.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.limbo.doorkeeper.api.constants.Intention;
//import org.limbo.doorkeeper.api.constants.Logic;
//import org.limbo.doorkeeper.api.constants.PolicyType;
//import org.limbo.doorkeeper.api.constants.SessionConstants;
//import org.limbo.doorkeeper.api.model.vo.*;
//import org.limbo.doorkeeper.api.model.vo.policy.PolicyRoleVO;
//import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
//import org.limbo.doorkeeper.server.constants.DoorkeeperConstants;
//import org.limbo.doorkeeper.server.dao.*;
//import org.limbo.doorkeeper.server.entity.*;
//import org.limbo.doorkeeper.server.service.policy.PolicyService;
//import org.limbo.doorkeeper.server.support.session.RedisSessionDAO;
//import org.limbo.doorkeeper.server.support.session.exception.SessionException;
//import org.limbo.doorkeeper.server.utils.EasyAntPathMatcher;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.ServletWebRequest;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.HandlerMapping;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//
///**
// * @author Devil
// * @date 2020/11/24 19:29
// */
//@Slf4j
//public class AuthenticationInterceptor implements HandlerInterceptor {
//
//    private final RedisSessionDAO redisSessionDAO;
//
//    @Autowired
//    private RealmMapper realmMapper;
//
//    @Autowired
//    private RoleMapper roleMapper;
//
//    @Autowired
//    private UserRoleMapper userRoleMapper;
//
//    @Autowired
//    private ResourceMapper resourceMapper;
//
//    @Autowired
//    private ClientMapper clientMapper;
//
//    @Autowired
//    private PermissionMapper permissionMapper;
//
//    @Autowired
//    private ResourceUriMapper resourceUriMapper;
//
//    @Autowired
//    private PermissionResourceMapper permissionResourceMapper;
//
//    @Autowired
//    private PolicyService policyService;
//
//    private static final ThreadLocal<EasyAntPathMatcher> PATH_MATCHER = ThreadLocal.withInitial(EasyAntPathMatcher::new);
//
//    public AuthenticationInterceptor(RedisSessionDAO redisSessionDAO) {
//        this.redisSessionDAO = redisSessionDAO;
//    }
//
//    /**
//     * 校验管理端权限 匹配 /admin/realm/**
//     */
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        // 判断 url 是否有对应权限
//        String sessionId = request.getHeader(SessionConstants.SESSION_HEADER);
//        SessionUser adminSession = redisSessionDAO.readSessionMayNull(sessionId);
//        if (adminSession == null) {
//            throw new SessionException("无有效会话");
//        }
//
//        Realm dkRealm = realmMapper.selectOne(Wrappers.<Realm>lambdaQuery()
//                .eq(Realm::getName, DoorkeeperConstants.REALM_NAME)
//        );
//
//        // 判断用户是否属于dk域
//        if (!dkRealm.getRealmId().equals(adminSession.getRealmId())) {
//            return false;
//        }
//
//        // 判断是不是DK的REALM admin
//        Role dkAdmin = roleMapper.getByName(dkRealm.getRealmId(), DoorkeeperConstants.REALM_CLIENT_DEFAULT_ID, DoorkeeperConstants.ADMIN);
//
//        UserRole userRole = userRoleMapper.selectOne(Wrappers.<UserRole>lambdaQuery()
//                .eq(UserRole::getUserId, adminSession.getUserId())
//                .eq(UserRole::getRoleId, dkAdmin.getRoleId())
//        );
//        if (userRole != null) {
//            return true;
//        }
//
//        // 判断uri权限
//        NativeWebRequest webRequest = new ServletWebRequest(request);
//        Map<String, String> uriTemplateVars = (Map<String, String>) webRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
//        String realmIdStr = uriTemplateVars.get(DoorkeeperConstants.REALM_ID);
//
//        Realm realm = realmMapper.selectById(Long.valueOf(realmIdStr));
//
//        // 获取对应的client
//        Client client = clientMapper.getByName(dkRealm.getRealmId(), realm.getName());
//
//        // 获取资源
//        List<ResourceVO> uriResource = getUriResource(client.getRealmId(), client.getClientId());
//
//        List<PermissionResource> permissionResources = new ArrayList<>();
//
//        for (ResourceVO resource : uriResource) {
//            if (CollectionUtils.isEmpty(resource.getUris())) {
//                continue;
//            }
//            for (ResourceUriVO uri : resource.getUris()) {
//                if (pathMatch(uri.getUri(), request.getRequestURI())) {
//                    // 匹配到了，则去找这个资源对应的权限
//
//                    List<PermissionResource> prs = permissionResourceMapper.selectList(Wrappers.<PermissionResource>lambdaQuery()
//                            .eq(PermissionResource::getResourceId, resource.getResourceId())
//                    );
//                    if (CollectionUtils.isNotEmpty(prs)) {
//                        permissionResources.addAll(prs);
//                    }
//                    break;
//                }
//            }
//        }
//
//        Set<Long> permissionIds = permissionResources.stream().map(PermissionResource::getPermissionId).collect(Collectors.toSet());
//
//        // 找到权限以及对应的策略
//        List<PermissionVO> permissions = new ArrayList<>();
//
//        Map<String, Set<PermissionVO>> intentionPermissions = permissions.stream().collect(Collectors.groupingBy(
//                PermissionVO::getIntention,
//                Collectors.mapping(p -> p, Collectors.toSet())
//        ));
//
//        // 找到拦截的权限 如果满足 返回false
//        Set<PermissionVO> re = intentionPermissions.get(Intention.REFUSE);
//        for (PermissionVO permissionVO : re) {
//            for (PermissionPolicyVO policy : permissionVO.getPolicys()) {
//                PolicyVO policyVO = policyService.get(client.getRealmId(), client.getClientId(), policy.getPolicyId());
//                PolicyType type = PolicyType.parse(policyVO.getType());
//                switch (type) {
//                    case ROLE:
//                        Logic logic = Logic.parse(policyVO.getLogic());
//                        switch (logic) {
//                            case DENY:
//                        }
//                        List<PolicyRoleVO> roles = policyVO.getRoles();
//                        // 判断用户是否拥有对应角色
//                        break;
//                    case USER:
//                        break;
//                    case PARAM:
//                        break;
//                }
//            }
//        }
//
//        // 找到放行的权限 如果满足 返回true
//
//        return false;
//    }
//
//    private List<ResourceVO> getUriResource(Long realmId, Long clientId) {
//
//        return null;
//    }
//
//    public boolean pathMatch(String pattern, String path) {
//        EasyAntPathMatcher antPathMatcher = PATH_MATCHER.get();
//        return antPathMatcher.match(StringUtils.trim(pattern), StringUtils.trim(path));
//    }
//
//}
