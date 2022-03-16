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
import org.limbo.doorkeeper.api.dto.param.add.RealmAddParam;
import org.limbo.doorkeeper.api.dto.param.update.RealmUpdateParam;
import org.limbo.doorkeeper.api.dto.vo.RealmVO;
import org.limbo.doorkeeper.api.dto.vo.ResponseVO;
import org.limbo.doorkeeper.api.dto.vo.RoleVO;
import org.limbo.doorkeeper.application.service.DoorkeeperService;
import org.limbo.doorkeeper.server.adapter.http.controller.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Devil
 * @since 2021/1/3 5:41 下午
 */
@Tag(name = "域")
@Slf4j
@RestController
@RequestMapping("/api/doorkeeper/v1/admin/realm")
public class AdminRealmController extends BaseController {

    @Resource
    private DoorkeeperService doorkeeperService;

    @Operation(summary = "新建域")
    @PostMapping
    public ResponseVO<Long> add(@RequestBody @Validated RealmAddParam param) {
        return ResponseVO.success(doorkeeperService.addRealm(getUser().getUserId(), param));
    }

    @Operation(summary = "域列表")
    @GetMapping
    public ResponseVO<List<RealmVO>> realms() {
        return ResponseVO.success(doorkeeperService.userRealms(getUser().getUserId()));
    }

    @Operation(summary = "域详情")
    @GetMapping("/{realmId}")
    public ResponseVO<RealmVO> get(@Validated @NotNull(message = "未提交域ID") @PathVariable("realmId") Long realmId) {
        return ResponseVO.success(doorkeeperService.get(realmId));
    }

    @Operation(summary = "更新域")
    @PutMapping("/{realmId}")
    public ResponseVO<RoleVO> update(@Validated @NotNull(message = "未提交域ID") @PathVariable("realmId") Long realmId,
                                     @Validated @RequestBody RealmUpdateParam param) {
        doorkeeperService.update(realmId, param);
        return ResponseVO.success();
    }

}
