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

package org.limbo.authc.admin.controller;

import org.limbo.authc.admin.dubbo.consumers.AuthorizationDubboConsumer;
import org.limbo.authc.admin.dubbo.consumers.RoleDubboConsumer;
import org.limbo.authc.admin.support.blog.BLog;
import org.limbo.authc.admin.support.blog.BLogType;
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.Param;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.AuthorizationVO;
import org.limbo.authc.api.interfaces.beans.vo.RoleVO;
import org.limbo.authc.api.interfaces.utils.verify.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/12 8:24 AM
 * @email brozen@qq.com
 */
@RestController
@RequestMapping("/role")
public class RoleController extends BaseController {

    @Autowired
    private RoleDubboConsumer roleDubboConsumer;

    @Autowired
    private AuthorizationDubboConsumer authorizationDubboConsumer;

    @PostMapping
    @BLog(name = "添加角色", type = BLogType.CREATE)
    public Response<RoleVO> addRole(@RequestBody RoleVO.AddParam param) {
        param.setProjectId(currentProjectId());
        return roleDubboConsumer.add(param);
    }

    @GetMapping
    public Response<List<RoleVO>> listRole() {
        return roleDubboConsumer.list(new Param(currentProjectId()));
    }

    @GetMapping("/query")
    public Response<Page<RoleVO>> queryRole(Page<RoleVO> param) {
        param.setProjectId(currentProjectId());
        return roleDubboConsumer.query(param);
    }

    @GetMapping("/{roleId}")
    public Response<RoleVO> getRole(@PathVariable("roleId") @Valid @NotNull(message = "角色不存在") Long roleId) {
        return roleDubboConsumer.get(new RoleVO.GetParam(currentProjectId(), roleId));
    }

    @PutMapping("/{roleId}")
    @BLog(expression = "'更新角色 '.concat(#arg1)", type = BLogType.UPDATE)
    public Response<RoleVO> updateRole(
            @RequestBody RoleVO.UpdateParam param,
            @PathVariable("roleId") @Valid @NotNull(message = "角色不存在") Long roleId
    ) {
        Verifies.equals(param.getRoleId(), roleId, "角色不存在");
        param.setProjectId(currentProjectId());
        return roleDubboConsumer.update(param);
    }

    @PutMapping("/{roleId}/grant")
    @BLog(expression = "'修改角色授权 '.concat(#arg0)", type = BLogType.UPDATE)
    public Response<Boolean> grantRole(
            @PathVariable("roleId") @Valid @NotNull(message = "角色不存在") Long roleId,
            @RequestBody AuthorizationVO.RoleGrantParam param) {
        Verifies.verify(roleId.equals(param.getRoleId()), "角色不存在");
        param.setProjectId(currentProjectId());
        param.setRoleId(roleId);
        return authorizationDubboConsumer.updateGrant(param);
    }

    @DeleteMapping("/{roleId}")
    @BLog(expression = "'删除角色 '.concat(#arg0)", type = BLogType.DELETE)
    public Response<Boolean> deleteRole(@PathVariable("roleId") @Valid @NotNull(message = "角色不存在") Long roleId) {
        return roleDubboConsumer.delete(new RoleVO.DeleteParam(currentProjectId(), roleId));
    }
}
