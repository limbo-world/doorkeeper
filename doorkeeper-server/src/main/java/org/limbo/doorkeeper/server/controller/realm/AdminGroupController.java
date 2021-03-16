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

package org.limbo.doorkeeper.server.controller.realm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.group.GroupAddParam;
import org.limbo.doorkeeper.api.model.param.group.GroupQueryParam;
import org.limbo.doorkeeper.api.model.param.group.GroupUpdateParam;
import org.limbo.doorkeeper.api.model.vo.GroupVO;
import org.limbo.doorkeeper.server.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.server.controller.BaseController;
import org.limbo.doorkeeper.server.service.GroupService;
import org.limbo.doorkeeper.server.support.GroupTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/5 4:46 下午
 */
@Tag(name = "用户组")
@Slf4j
@RestController
@RequestMapping("/api/admin/realm/{realmId}/group")
public class AdminGroupController extends BaseController {

    @Autowired
    private GroupService groupService;

    @Operation(summary = "新建用户组")
    @PostMapping
    public Response<GroupVO> add(@RequestBody @Validated GroupAddParam param) {
        return Response.success(groupService.add(getRealmId(), param));
    }

    @Operation(summary = "返回所有用户组")
    @GetMapping
    public Response<List<GroupVO>> list(GroupQueryParam param) {
        List<GroupVO> groups = groupService.list(getRealmId(), param);
        if (DoorkeeperConstants.TREE.equals(param.getReturnType())) {
            return Response.success(GroupTool.organizeGroupTree(null, groups));
        }
        return Response.success(groups);
    }

    @Operation(summary = "根据id查询用户组")
    @GetMapping("/{groupId}")
    public Response<GroupVO> getById(@Validated @NotNull(message = "未提交用户组ID") @PathVariable("groupId") Long groupId) {
        return Response.success(groupService.getById(getRealmId(), groupId));
    }

    @Operation(summary = "更新用户组")
    @PutMapping("/{groupId}")
    public Response<Void> update(@Validated @NotNull(message = "未提交用户组ID") @PathVariable("groupId") Long groupId,
                                   @Validated @RequestBody GroupUpdateParam param) {
        groupService.update(getRealmId(), groupId, param);
        return Response.success();
    }

    @Operation(summary = "删除用户组")
    @DeleteMapping("/{groupId}")
    public Response<Void> delete(@Validated @NotNull(message = "未提交用户组ID") @PathVariable("groupId") Long groupId) {
        groupService.delete(getRealmId(), groupId);
        return Response.success();
    }

}
