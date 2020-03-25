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

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.limbo.authc.admin.beans.po.AdminProjectPO;
import org.limbo.authc.admin.beans.vo.AdminAccountProjectVO;
import org.limbo.authc.admin.service.AdminAccountProjectService;
import org.limbo.authc.admin.service.AdminProjectService;
import org.limbo.authc.admin.support.blog.BLog;
import org.limbo.authc.api.interfaces.beans.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author devil
 * @date 2020/3/13
 */
@RestController
@RequestMapping("/admin-project")
public class AdminProjectController extends BaseController {

    @Autowired
    private AdminAccountProjectService adminAccountProjectService;
    @Autowired
    private AdminProjectService adminProjectService;

    /**
     * 获取管理员拥有哪些未删除的项目
     */
    @GetMapping("/{accountId}")
    public Response<List<AdminProjectPO>> list(@Validated @PathVariable("accountId") @NotNull(message = "账户") Long accountId) {
        return Response.ok(adminAccountProjectService.getByAccount(accountId));
    }

    /**
     * 获取所有未删除的项目
     */
    @GetMapping
    public Response<List<AdminProjectPO>> all() {
        return Response.ok(adminProjectService.list(Wrappers.emptyWrapper()));
    }

    /**
     * 更新账户的项目
     */
    @PutMapping("/{accountId}")
    @BLog(expression = "'修改账户 ' + #arg1 + ' 拥有项目'")
    public Response<Boolean> update(
            @RequestBody @Validated AdminAccountProjectVO.UpdateParam param,
            @Validated @PathVariable("accountId") @NotNull(message = "账户不存在") Long accountId
    ) {
        adminAccountProjectService.updateAccountProjects(accountId, param.getProjectIds());
        return Response.ok(true);
    }
}
