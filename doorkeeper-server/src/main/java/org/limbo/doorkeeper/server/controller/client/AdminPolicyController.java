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

package org.limbo.doorkeeper.server.controller.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.policy.PolicyAddParam;
import org.limbo.doorkeeper.api.model.param.policy.PolicyBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.policy.PolicyQueryParam;
import org.limbo.doorkeeper.api.model.param.policy.PolicyUpdateParam;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.controller.BaseController;
import org.limbo.doorkeeper.server.service.policy.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @author Devil
 * @date 2021/1/5 4:46 下午
 */
@Tag(name = "策略")
@Slf4j
@RestController
@RequestMapping("/api/admin/realm/{realmId}/client/{clientId}/policy")
public class AdminPolicyController extends BaseController {

    @Autowired
    private PolicyService policyService;

    @Operation(summary = "新建策略")
    @PostMapping
    public Response<PolicyVO> add(@RequestBody @Validated PolicyAddParam param) {
        return Response.success(policyService.add(getRealmId(), getClientId(), param));
    }

    @Operation(summary = "批量修改策略")
    @PostMapping("/batch")
    public Response<Void> batch(@RequestBody @Validated PolicyBatchUpdateParam param) {
        policyService.batchUpdate(getRealmId(), getClientId(), param);
        return Response.success();
    }

    @Operation(summary = "分页查询策略")
    @GetMapping
    public Response<Page<PolicyVO>> page(@Validated PolicyQueryParam param) {
        return Response.success(policyService.page(getRealmId(), getClientId(), param));
    }

    @Operation(summary = "查询策略")
    @GetMapping("/{policyId}")
    public Response<PolicyVO> get(@Validated @NotNull(message = "未提交策略ID") @PathVariable("policyId") Long policyId) {
        return Response.success(policyService.get(getRealmId(), getClientId(), policyId));
    }

    @Operation(summary = "更新策略")
    @PutMapping("/{policyId}")
    public Response<Void> update(@Validated @NotNull(message = "未提交策略ID") @PathVariable("policyId") Long policyId,
                                   @Validated @RequestBody PolicyUpdateParam param) {
        policyService.update(getRealmId(), getClientId(), policyId, param);
        return Response.success();
    }
}
