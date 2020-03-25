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

import org.limbo.authc.admin.dubbo.consumers.PermissionDubboConsumer;
import org.limbo.authc.admin.support.blog.BLog;
import org.limbo.authc.admin.support.blog.BLogType;
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.Param;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.PermissionVO;
import org.limbo.authc.api.interfaces.utils.verify.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/9 4:10 PM
 * @email brozen@qq.com
 */
@RestController
@RequestMapping("/permission")
public class PermissionController extends BaseController {

    @Autowired
    private PermissionDubboConsumer permissionDubboConsumer;

    @PostMapping
    @BLog(name = "添加权限", type = BLogType.CREATE)
    public Response<PermissionVO> addPermission(@RequestBody PermissionVO.AddParam param) {
        param.setProjectId(currentProjectId());
        return permissionDubboConsumer.add(param);
    }

    @GetMapping
    public Response<List<PermissionVO>> listPermission() {
        return permissionDubboConsumer.list(new Param(currentProjectId()));
    }

    @GetMapping("/query")
    public Response<Page<PermissionVO>> queryPermission(PermissionVO.QueryParam param) {
        param.setProjectId(currentProjectId());
        return permissionDubboConsumer.query(param);
    }

    @GetMapping("/{permCode}")
    public Response<PermissionVO> getPermission(@PathVariable("permCode") @Validated @NotBlank(message = "权限不存在") String permCode) {
        return permissionDubboConsumer.get(new PermissionVO.GetParam(currentProjectId(), permCode));
    }

    @PutMapping("/{permCode}")
    @BLog(expression = "'更新权限 '.concat(#arg1)", type = BLogType.UPDATE)
    public Response<PermissionVO> updatePermission(
            @RequestBody PermissionVO.UpdateParam param,
            @PathVariable("permCode") @Validated @NotBlank(message = "权限不存在") String permCode
    ) {
        Verifies.equals(param.getPermCode(), permCode, "权限不存在");
        param.setProjectId(currentProjectId());
        return permissionDubboConsumer.update(param);
    }

    @DeleteMapping("/{permCode}")
    @BLog(expression = "'删除权限 '.concat(#arg0)", type = BLogType.DELETE)
    public Response<Boolean> deletePermission(@PathVariable("permCode") @Validated @NotBlank(message = "权限不存在") String permCode) {
        return permissionDubboConsumer.delete(new PermissionVO.DeleteParam(currentProjectId(), permCode));
    }

}
