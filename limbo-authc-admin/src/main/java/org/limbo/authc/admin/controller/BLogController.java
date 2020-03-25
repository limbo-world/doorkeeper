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

import org.limbo.authc.admin.beans.vo.AdminBLogVO;
import org.limbo.authc.admin.service.AdminBLogService;
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Brozen
 * @date 2020/3/24 10:54 AM
 * @email brozen@qq.com
 */
@RestController
@RequestMapping("/b-log")
public class BLogController extends BaseController {

    @Autowired
    private AdminBLogService bLogService;

    @GetMapping
    public Response<Page<AdminBLogVO>> query(AdminBLogVO.QueryParam param) {
        return Response.ok(bLogService.queryBLog(param));
    }

}
