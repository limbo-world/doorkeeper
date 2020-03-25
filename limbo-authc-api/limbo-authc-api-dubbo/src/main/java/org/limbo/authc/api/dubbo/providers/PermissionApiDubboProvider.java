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
import org.limbo.authc.api.interfaces.apis.PermissionApi;
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.Param;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.PermissionVO;
import org.limbo.authc.core.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/6 9:03 AM
 * @email brozen@qq.com
 */
@Service(version = "${dubbo.service.version}", validation="true")
public class PermissionApiDubboProvider extends BaseProvider implements PermissionApi {

    @Autowired
    private PermissionService permissionService;

    @Override
    public Response<PermissionVO> add(PermissionVO.AddParam param) {
        return Response.ok(permissionService.addPermission(param));
    }

    @Override
    public Response<PermissionVO> update(PermissionVO.UpdateParam param) {
        permissionService.updatePermission(param);
        return Response.ok(permissionService.getPermission(param.getProjectId(), param.getPermCode()));
    }

    @Override
    public Response<Boolean> delete(PermissionVO.DeleteParam param) {
        return Response.ok(permissionService.deletePermission(param.getProjectId(), param.getPermCode()) > 0);
    }

    @Override
    public Response<List<PermissionVO>> list(Param param) {
        return Response.ok(permissionService.listPermission(param.getProjectId()));
    }

    @Override
    public Response<Page<PermissionVO>> query(PermissionVO.QueryParam param) {
        return Response.ok(permissionService.queryPermission(param));
    }

    @Override
    public Response<PermissionVO> get(PermissionVO.GetParam param) {
        return Response.ok(permissionService.getPermission(param.getProjectId(), param.getPermCode()));
    }
}
