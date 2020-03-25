/*
 *  Copyright 2020-2024 Limbo Team (https://github.com/LimboHome).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.limbo.authc.api.dubbo.providers;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.dubbo.config.annotation.Service;
import org.limbo.authc.api.interfaces.apis.AuthorizationApi;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.po.AccountPO;
import org.limbo.authc.api.interfaces.beans.vo.AuthorizationVO;
import org.limbo.authc.api.interfaces.beans.vo.MenuVO;
import org.limbo.authc.api.interfaces.beans.vo.PermissionVO;
import org.limbo.authc.core.service.AccountService;
import org.limbo.authc.core.service.GrantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 授权相关接口
 *
 * @author Brozen
 * @date 2020/3/7 4:28 PM
 * @email brozen@qq.com
 */
@Service(version = "${dubbo.service.version}", validation="true")
public class AuthorizationApiDubboProvider extends BaseProvider implements AuthorizationApi {

    @Autowired
    private GrantService grantService;
    @Autowired
    private AccountService accountService;

    @Override
    public Response<Boolean> grant(AuthorizationVO.GrantParam param) {
        grantService.grantRole(param);
        return Response.ok(true);
    }

    @Override
    public Response<Boolean> revoke(AuthorizationVO.GrantParam param) {
        grantService.revokeRole(param);
        return Response.ok(true);
    }

    @Override
    public Response<Boolean> updateGrant(AuthorizationVO.RoleGrantParam param) {
        grantService.updateGrant(param);
        return Response.ok(true);
    }

    @Override
    public Response<Boolean> updateGrant(AuthorizationVO.AccountGrantParam param) {
        grantService.updateGrant(param);
        return Response.ok(true);
    }

    // TODO 增加缓存
    @Override
    public Response<Boolean> hasPermission(AuthorizationVO.PermissionCheckParam param) {
        AccountPO account = accountService.getById(param.getAccountId());

        // 如果是这个项目的超级管理员，则直接拥有这个项目的所有权限
        if (BooleanUtils.isTrue(account.getIsSuperAdmin()) && account.getProjectId().equals(getProjectId())) {
            return Response.ok(true);
        }

        // 获取此账号在对应应用中的权限
        List<PermissionVO> permissionVOS = grantService.getAccountPermissions(getProjectId(), param.getAccountId());

        // 如果权限列表为空 直接返回失败
        if (CollectionUtils.isEmpty(permissionVOS)) {
            return Response.ok(false);
        }

        // TODO 这个 methodUrls 可以缓存下
        // 获取用户API权限列表 get urls
        Map<String, List<String>> methodUrls = new HashMap<>(16);
        for (PermissionVO permissionVO : permissionVOS) {
            List<String> apis = permissionVO.getApiList();
            if (CollectionUtils.isEmpty(apis)) continue; // apis字段可能为空
            for (String api : apis) {
                String[] apiSplit = api.split(" ");
                if (apiSplit.length <= 1) { // 如果没有设置请求类型 则对所有请求类型开放
                    HttpMethod[] methods = HttpMethod.values();
                    for (HttpMethod method : methods) {
                        methodUrls.computeIfAbsent(method.name(), k -> new ArrayList<>());
                        methodUrls.get(method.name()).add(apiSplit[0]);
                    }
                } else {
                    String methodName = apiSplit[0].toUpperCase();
                    methodUrls.computeIfAbsent(methodName, k -> new ArrayList<>());
                    methodUrls.get(methodName).add(apiSplit[1]);
                }
            }
        }

        // 获取对应请求类型的urls
        List<String> urlPatterns = methodUrls.get(param.getHttpMethod().toUpperCase());
        if (CollectionUtils.isEmpty(urlPatterns)) {
            return Response.ok(false);
        }

        // 如果param中的path ant 匹配 urls中的一个就返回拥有权限
        AntPathMatcher matcher = new AntPathMatcher();
        for (String urlPattern : urlPatterns) {
            if (matcher.match(urlPattern, param.getPath())) {
                // 如果匹配到权限
                return Response.ok(true);
            }
        }

        return Response.ok(false);
    }

    @Override
    public Response<List<MenuVO>> getMenus(AuthorizationVO.GetMenusParam param) {
        return Response.ok(grantService.getAccountMenus(param.getProjectId(), param.getAccountId()));
    }
}
