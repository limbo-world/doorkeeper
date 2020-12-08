/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.limbo.doorkeeper.server.service;

import org.limbo.doorkeeper.api.model.param.AuthenticationCheckParam;
import org.limbo.doorkeeper.api.model.vo.AccountGrantVO;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.limbo.doorkeeper.api.model.vo.RoleVO;

import java.util.List;
import java.util.Set;

/**
 * @author liuqingtong
 * @date 2020/11/25 18:08
 */
public interface AuthenticationService {

    /**
     * 检查是否有权访问资源
     */
    Boolean accessAllowed(Long projectId, AuthenticationCheckParam param);

    /**
     * 获取用户的授权信息，包括授予的角色、权限，不包括API
     */
    AccountGrantVO getGrantInfo(Long projectId, Long accountId);

    /**
     * 获取用户赋予的角色
     */
    List<RoleVO> getGrantedRoles(Long projectId, Long accountId);

    /**
     * 获取用户此项目管理端角色
     */
    List<RoleVO> getGrantedAdminRoles(Long projectId, Long accountId);

    /**
     * 获取用户授予的权限
     */
    AccountGrantVO getGrantedPermissions(Long projectId, Long accountId);

    /**
     * 获取用户授予的管理端权限
     */
    AccountGrantVO getGrantedAdminPermissions(Long projectId, Long accountId);

    /**
     * 获取用户授予的权限
     */
    AccountGrantVO getGrantedPermissionsByRoles(Set<Long> roleIds);

    /**
     * 检测配置与请求检查参数的请求url是否匹配，检测method和path两部分
     */
    boolean permissionMatch(PermissionVO permission, AuthenticationCheckParam param);

    boolean pathMatch(String pattern, String path);
}
