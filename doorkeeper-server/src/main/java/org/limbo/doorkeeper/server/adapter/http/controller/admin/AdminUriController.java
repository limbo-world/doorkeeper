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

package org.limbo.doorkeeper.server.adapter.http.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.model.vo.ResponseVO;
import org.limbo.doorkeeper.api.model.param.query.UriQueryParam;
import org.limbo.doorkeeper.api.model.vo.UriVO;
import org.limbo.doorkeeper.server.adapter.http.controller.BaseController;
import org.limbo.doorkeeper.server.service.UriService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Devil
 * @since 2021/4/19 8:23 下午
 */
@Tag(name = "uri")
@Slf4j
@RestController
@RequestMapping("/api/admin/realm/{realmId}/client/{clientId}/uri")
public class AdminUriController extends BaseController {

    @Autowired
    private UriService uriService;

    @Operation(summary = "查询uri")
    @GetMapping
    public ResponseVO<List<UriVO>> list(@ParameterObject UriQueryParam param) {
        return ResponseVO.success(uriService.list(getRealmId(), getClientId(), param));
    }
}
