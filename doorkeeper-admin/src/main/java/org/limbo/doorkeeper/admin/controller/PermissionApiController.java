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

package org.limbo.doorkeeper.admin.controller;

import org.limbo.doorkeeper.api.client.PermissionApiClient;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.PermissionApiAddParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/20 10:41 AM
 */
@RestController
@RequestMapping("/permission-api")
public class PermissionApiController {

    @Autowired
    private PermissionApiClient permissionApiClient;


    @PostMapping
    public Response<Boolean> addPermissionApi(@RequestBody List<PermissionApiAddParam> permissionApis) {
        return permissionApiClient.addPermissionApi(permissionApis);
    }


    @DeleteMapping
    public Response<Integer> deletePermissionApi(@RequestBody List<Long> permissionApiIds){
        return permissionApiClient.deletePermissionApi(permissionApiIds);
    }
}
