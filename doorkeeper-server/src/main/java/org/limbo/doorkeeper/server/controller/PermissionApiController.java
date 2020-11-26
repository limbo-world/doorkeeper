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

package org.limbo.doorkeeper.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.PermissionApiAddParam;
import org.limbo.doorkeeper.api.model.param.PermissionApiQueryParam;
import org.limbo.doorkeeper.api.model.vo.PermissionApiVO;
import org.limbo.doorkeeper.server.service.PermissionApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/20 10:41 AM
 */
@Tag(name = "权限api关系")
@RestController
@RequestMapping("/permission-api")
public class PermissionApiController extends BaseController {

    @Autowired
    private PermissionApiService permissionApiService;

    @GetMapping
    @Operation(summary = "获取权限api")
    public Response<List<PermissionApiVO>> list(PermissionApiQueryParam param) {
        return Response.ok(permissionApiService.list(getProjectId(), param));
    }

    @PostMapping
    @Operation(summary = "绑定权限api")
    public Response<Boolean> addPermissionApi(@RequestBody List<PermissionApiAddParam> permissionApis) {
        permissionApiService.addPermissionApi(getProjectId(), permissionApis);
        return Response.ok(true);
    }


    @DeleteMapping
    @Operation(summary = "删除权限api")
    public Response<Integer> deletePermissionApi(@RequestBody List<Long> permissionApiIds){
        return Response.ok(permissionApiService.deletePermissionApi(getProjectId(), permissionApiIds));
    }
}
