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
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.vo.AccountGrantVO;
import org.limbo.doorkeeper.api.model.vo.AccountProjectVO;
import org.limbo.doorkeeper.server.service.AccountProjectService;
import org.limbo.doorkeeper.server.service.AuthenticationService;
import org.limbo.doorkeeper.server.support.session.AbstractSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author devil
 * @date 2020/3/11
 */
@Slf4j
@Tag(name = "会话")
@RestController
@RequestMapping("/session")
public class SessionController extends BaseController {

//    @Autowired
//    private ProjectClient projectClient;
//    @Autowired
//    private AuthenticationClient authenticationClient;
//    @Autowired
//    private AdminProjectService adminProjectService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private AccountProjectService accountProjectService;

    @GetMapping
    @Operation(summary = "获取会话")
    public Response<AbstractSession> session() {
        AbstractSession session = getSession();
        return Response.ok(session);
    }
//
//    /**
//     * 切换用户当前选择的项目
//     */
//    @PutMapping("/project/{projectId}")
//    public Response<AdminSession> switchProject(@PathVariable("projectId") @Valid @NotNull(message = "项目不存在") Long projectId) {
//        AdminSession session = getSession();
//        SessionAccount account = session.getAccount();
//
//        Response<ProjectVO> projectRes = projectClient.getProject(projectId);
//        if (!projectRes.ok()) {
//            Response<AdminSession> response = new Response<>();
//            response.setCode(projectRes.getCode());
//            response.setMsg(projectRes.getMsg());
//            return response;
//        }
//
//        if (!session.getAccount().getIsAdmin()) {
//            // 检测是否有项目的操作权限
//            AdminProject accountProject = adminProjectService.getByAccountProject(account.getAccountId(), projectId);
//            if (accountProject == null) {
//                return Response.paramError("无权操作此项目！");
//            }
//        }
//        ProjectVO project = projectRes.getData();
//        account.setCurrentProjectId(project.getProjectId());
//        account.setCurrentProjectName(project.getProjectName());
//        sessionDAO.save(session);
//        return Response.ok(getSession());
//    }
//

    @Operation(summary = "获取会话权限信息")
    @GetMapping("/grant-info")
    public Response<AccountGrantVO> getGrantInfo() {
        return Response.ok(authenticationService.getGrantInfo(0L, getSessionAccount().getAccountId()));
    }

    @Operation(summary = "会话项目列表")
    @GetMapping("/project")
    public Response<List<AccountProjectVO>> list() {
        return Response.ok(accountProjectService.sessionProject(getSessionAccount().getAccountId()));
    }

    @GetMapping("/logout")
    public Response<Boolean> logout() {
        AbstractSession session = getSession();
        sessionDAO.destroySession(session.getToken());
        return Response.ok(true);
    }

}
