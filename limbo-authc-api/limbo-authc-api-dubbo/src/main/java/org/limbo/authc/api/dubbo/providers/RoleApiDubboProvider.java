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

import org.apache.dubbo.config.annotation.Service;
import org.limbo.authc.api.interfaces.apis.RoleApi;
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.Param;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.RoleVO;
import org.limbo.authc.core.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 角色API
 *
 * @author Brozen
 * @date 2020/3/7 4:08 PM
 * @email brozen@qq.com
 */
@Service(version = "${dubbo.service.version}", validation = "true")
public class RoleApiDubboProvider extends BaseProvider implements RoleApi {

    @Autowired
    private RoleService roleService;

    @Override
    public Response<RoleVO> add(RoleVO.AddParam param) {
        Long roleId = roleService.addRole(param);
        return Response.ok(roleService.getRole(param.getProjectId(), roleId));
    }

    @Override
    public Response<RoleVO> update(RoleVO.UpdateParam param) {
        roleService.updateRole(param);
        return Response.ok(roleService.getRole(param.getProjectId(), param.getRoleId()));
    }

    @Override
    public Response<Boolean> delete(RoleVO.DeleteParam param) {
        return Response.ok(roleService.deleteRole(param.getProjectId(), param.getRoleId()) > 0);
    }

    @Override
    public Response<List<RoleVO>> list(Param param) {
        return Response.ok(roleService.listRole(param.getProjectId()));
    }

    @Override
    public Response<Page<RoleVO>> query(Page<RoleVO> param) {
        return Response.ok(roleService.queryRole(param));
    }

    @Override
    public Response<RoleVO> get(RoleVO.GetParam param) {
        return Response.ok(roleService.getRole(param.getProjectId(), param.getRoleId()));
    }
}
